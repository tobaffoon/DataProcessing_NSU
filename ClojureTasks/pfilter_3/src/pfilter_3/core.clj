(ns pfilter-3.core
  (:gen-class))


(defn even+ [number]
  (Thread/sleep 1)
  (even? number))

; Не ленивые вычисления для конечных последовательностей 
(defn p-filter-finite
  ([pred coll]
   (let [chunk-size (int (Math/ceil (Math/sqrt (count coll)))),

         parts (partition-all chunk-size coll)]
     (->> parts
          (map (fn [coll1]

                 (future (vec (filter pred coll1)))))

          ;;do not forget to cancel map’s laziness!
          (doall) ; запускает вычисления future. Без него Future вызывался бы только в момент доставания определённого элемента, то есть последовательно, без параллелизма
          (map deref)
          (reduce concat))))) ; без concat будет list векторов. Вектор - результат фильтрации каждой part 

(defn p-filter-infinite_lazy
  ([pred coll] (p-filter-infinite_lazy pred coll 256))
  ([pred coll chunk-size] ; pred - предикат, coll - seq
   (let [parts (partition-all chunk-size coll)] ; последовательность последовательностей. Просто так такая операция ленивой не будет
     (mapcat identity ; используем ленивость map. Без конкат будет list из list (каждая часть в своём list), а reduce concat удалит всякую ленивость
             (->> parts ; разбиваем по частям
                  (map (fn [part] ; применяем к каждой части
                         (future (p-filter-finite pred part)))) ; фильтр, где каждая часть вычислиться в отдельное future
                  (map deref)))))) ; достаём значения из future

(defn -main
  []
  (println (time (count (filter even? (range 1 1000))))) ; без count он печатает весь результат фильтрации
  (println (time (count (p-filter-infinite_lazy even? (range 1 1000) 1024))))
  (println (time (count (p-filter-infinite_lazy even? (range 1 1000) 256))))
  (println (time (count (p-filter-infinite_lazy even? (range 1 1000) 64))))
  (println (time (count (p-filter-infinite_lazy even? (range 1 1000) 16))))
  (println (take 2 (p-filter-infinite_lazy even? (range))))
  (shutdown-agents))

