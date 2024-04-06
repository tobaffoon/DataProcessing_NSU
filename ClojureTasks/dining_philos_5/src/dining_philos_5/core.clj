(ns dining-philos-5.core
  (:gen-class)
  (:import java.util.Random))

(def printer (agent nil))

(defn print-msg
  "Prints synchroniously"
  [_ str]
  (prn str))

(def restartCounter (atom 0))

(defn log
  [& data]
  (send printer print-msg (apply str data)))

(defn genForks
  "returns vector of ref to integers each representing a counter of times that i_th fork was picked"
  [nforks]
  (->> (repeat nforks 0)
       (map ref)
       vec))

(defn philo_run
  "Infinitely looped function representing dining philosopher's activities"
  [index forks ndishes think_min_ms think_max_ms eat_min_ms eat_max_ms]
  (let [max_id (- (count forks) 1)

        leftForkId (if (== index 0)
                     max_id
                     (- index 1))
        leftFork (nth forks leftForkId)

        rightForkId index
        rightFork (nth forks rightForkId)

        randomGen (new Random index)
        getRandBoundedTime (fn [min_ms max_ms] (.nextLong randomGen min_ms max_ms))]
    (doseq [dish_id (vec (range 1 (+ ndishes 1)))]
      (log "Forks state: " (vec (map deref forks)))
      (log "Philosopher " index " is thinking now")
      (Thread/sleep (getRandBoundedTime think_min_ms think_max_ms))
      (dosync
       (swap! restartCounter inc)
       (Thread/sleep 100)
       (alter leftFork inc)
       (log "Philosopher " index " got " leftForkId "th fork to their LEFT")
       (alter rightFork inc)
       (log "Philosopher " index " got " rightForkId "th fork to their RIGHT")
       (log "Philosopher " index " eats for the " dish_id "th time")
       (Thread/sleep (getRandBoundedTime eat_min_ms eat_max_ms))))
      (log "Philosopher " index " stops eating"))
  (log "Forks state: " (vec (map deref forks)))
  (log "Philosopher " index " finished eating")
  nil)

;lein run 2 1 2 1 2 1
; Идея с live lock - берут по одной вилке и пытаются взять вторую, не кладя первую.
(defn unsafe_philo_run
  "Grab one fork and hold it until you get a second one"
  [index forks ndishes think_min_ms think_max_ms eat_min_ms eat_max_ms]
  (let [max_id (- (count forks) 1)

        leftForkId (if (== index 0)
                     max_id
                     (- index 1))
        leftFork (nth forks leftForkId)

        rightForkId index
        rightFork (nth forks rightForkId)

        randomGen (new Random index)
        getRandBoundedTime (fn [min_ms max_ms] (.nextLong randomGen min_ms max_ms))]
    (doseq [dish_id (vec (range 1 (+ ndishes 1)))]
      (log "Forks state: " (vec (map deref forks)))
      (log "Philosopher " index " is thinking now")
      (Thread/sleep (getRandBoundedTime think_min_ms think_max_ms))
      (dosync
       (swap! restartCounter inc)
       (alter leftFork inc)
       (log "Philosopher " index " got " leftForkId "th fork to their LEFT"))
      (dosync
       (Thread/sleep 1000)
       (alter rightFork inc)
       (log "Philosopher " index " got " rightForkId "th fork to their RIGHT")
       (log "Philosopher " index " eats for the " dish_id "th time")
       (Thread/sleep (getRandBoundedTime eat_min_ms eat_max_ms))
       (log "Philosopher " index " stops eating"))))
  (log "Forks state: " (vec (map deref forks)))
  (log "Philosopher " index " finished eating")
  nil)

(defn monitorRestarts
  [ndishes forks]
  (while (some (fn [fork] (< fork (* ndishes 2))) (vec (map deref forks)))
    (do ; add thread that will print number of restarts each second
      (Thread/sleep 1000)
      (log "-------------------RestartCounter: " @restartCounter "-------------------"))))

; Возможно нечётные немного больше рестартов провоцируют, но сильно разницы не заметно (lein run 10 100 300 100 300 9    lein run 9 100 300 100 300 10)
; lein run 5 100 1000 100 1000 3 
; lein run 4 100 1000 100 1000 4 
(defn -main
  "args: 
   [0] - number of philosohpers;
   [1] - minimal time of thinking for philosopher
   [2] - maximal time of thinking for philosopher
   [3] - minimal time of eating for philosopher
   [4] - maximal time of eating for philosopher
   [5] - amount of times each philosopher should eat"
  [& args]
  (let [nphilos (Integer/parseInt (first args))
        forks (genForks nphilos)
        think_min_ms (Integer/parseInt (nth args 1))
        think_max_ms (Integer/parseInt (nth args 2))
        eat_min_ms (Integer/parseInt (nth args 3))
        eat_max_ms (Integer/parseInt (nth args 4))
        ndishes (Integer/parseInt (nth args 5))]
    
      (time (dorun  ; produce all side effects of calling philo_run
       (apply pcalls ; run functions in parallel
        (conj
         (for [i (range nphilos)] ; get nphilos functions
           #(philo_run i forks ndishes think_min_ms think_max_ms eat_min_ms eat_max_ms))
         #(monitorRestarts ndishes forks)
         ))))))
