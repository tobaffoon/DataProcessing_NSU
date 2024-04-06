(ns strings.core
  (:gen-class))

 ; удлинить строку fst на один символ из алфавита 
  (defn concat-all-single [fst alphabet]
    (map
     #(str fst %) ; конкатенировать выбранные символы из алфавита с исходной строкой
     (filter #(not= (last fst) (first %)) alphabet))) ; из символов из алфавита (first так как в алфавите строки) найти такие, на которые не кончается текущая строка

  (defn concat-all-list [list alphabet]
    (mapcat ;mapcat соединяет результаты map. Так как concat-all-single возвращает список с пребавленным символом для каждого слова (комбинация слов и добавленного символа). Эти списки можно конкатенировать
     #(concat-all-single % alphabet)
     list)) ; list - строки, в начале каждая строка это одна буква из алфавита

; создать такие цепочки из алфавита, что нет 2х подряд идущих одинаковых букв
  (defn alphabet-strings-chain [alphabet length]
    {:pre [(>= length 0)]}
    (->> (range 1 length) ; имитация цикла - 2-й аргумент для функции reduce, выполняется length раз (на каждой итерации добавляется по символу)
         (reduce          ; начальный acc - алфавит
          (fn [acc _]     ; length раз применяем к acc функцию concat-all-list
            (concat-all-list acc alphabet)) ; 
          alphabet)))

  (defn -main 
    ([len] (println
     (alphabet-strings-chain ["a", "b", "c"] len)))
    ([] (println
     (alphabet-strings-chain ["a", "b", "c"] 2)))
    )
