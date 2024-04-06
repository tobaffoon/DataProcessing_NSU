(ns prod-line-4.core
  (:require [clojure.pprint :as pp]))

(declare supply-msg)
(declare notify-msg)
(declare print-msg)

(def printer (agent nil))

(defn print-msg
  "Prints synchroniously"
  [state str]
  (println str))

(defn storage
  "Creates a new storage
   ware - a name of ware to store (string)
   notify-step - amount of stored items required for logger to react. 0 means no logging 
   consumers - factories to notify when the storage is updated
   returns a map that contains:
     :storage - an atom to store items that can be used by factories directly
     :ware - a stored ware name
     :worker - an agent to send supply-msg"
  [ware notify-step & consumers]
  (let [counter (atom 0 
                      :validator #(>= % 0)
                      ),
        worker-state {:storage counter,
                      :ware ware,
                      :notify-step notify-step,
                      :consumers consumers}]
    {:storage counter,
     :ware ware,
     :worker (agent worker-state)}))

(defn factory
  "Creates a new factory
   amount - number of items produced per cycle
   duration - cycle duration in milliseconds
   target-storage - a storage to put products with supply-msg
   ware-amounts - a list of ware names and their amounts required for a single cycle
   returns a map that contains:
     :worker - an agent to send notify-msg"
  [amount duration target-storage & ware-amounts]
  (let [bill (apply hash-map ware-amounts),
        buffer (reduce-kv (fn [acc k _] (assoc acc k 0)) ; creates a hash-map that contains all the ware names and 0s associated with them 
                          {} bill),
        ;;a state of factory agent:
        ;;  :amount - a number of items to produce per cycle
        ;;  :duration - a duration of cylce 
        ;;  :target-storage - a storage to place products (via supply-msg to its worker)
        ;;  :bill - a map with ware names as keys and their amounts of values
        ;;     shows how many wares must be consumed to perform one production cycle
        ;;  :buffer - a map with similar structure as for :bill that shows how many wares are already collected;
        ;;     it is the only mutable part. 
        worker-state {:amount amount,
                      :duration duration,
                      :target-storage target-storage,
                      :bill bill,
                      :buffer buffer}]
    {:worker (agent worker-state)}))

(defn source
  "Creates a source that is a thread that produces 'amount' of wares per cycle to store in 'target-storage'
   and with given cycle 'duration' in milliseconds
   returns Thread that must be run explicitly"
  [amount duration target-storage]
  (new Thread
       (fn []
         (Thread/sleep duration)
         (send (target-storage :worker) supply-msg amount)
         (recur))))

(defn supply-msg
  "A message that can be sent to a storage worker to notify that the given 'amount' of wares should be added.
   Adds the given 'amount' of ware to the storage and notifies all the registered factories about it 
   state - see code of 'storage' for structure"
  [state amount]
  (swap! (state :storage) #(+ % amount))      ;update counter, could not fail  
  (let [ware (state :ware),
        cnt @(state :storage),
        notify-step (state :notify-step),
        consumers (state :consumers)]
    ;;logging part, notify-step == 0 means no logging
    ;; (when (and (> notify-step 0)
    ;;            (> (int (/ cnt notify-step)) ; cnt / ns = сколько порций материала уже можно забрать (counter обновился)
    ;;               (int (/ (- cnt amount) notify-step)))) ; (cnt - amount) / ns = сколько порций было до поставки 
    (when (> notify-step 0)
      (send printer print-msg (str (.format (new java.text.SimpleDateFormat "hh.mm.ss.SSS") (new java.util.Date)) " <| " ware " supplied amount: " cnt))
    ;;factories notification part
      (when consumers ; если список использующих фабрик != nil
        (doseq [consumer (shuffle consumers)]
          (send printer print-msg (str "Notify LOG: " "\n Storage = " @(state :storage)))
          (send (consumer :worker) notify-msg ware (state :storage) amount)
          ))))
  state)                 ;worker itself is immutable, keeping configuration only



(defn bufferMeetsBill
  "Checks if all the materials in buffer meet requirements of buffer"
  [buffer bill]
  (every? (fn [ware]
            (let [wareName (key ware)
                  wareStored (val ware)
                  wareNeeded (get bill wareName)]
              (>= wareStored wareNeeded))) buffer)) ; check if there is at least as much stored ware as needed ware

(defn useBufferedMaterials
  "Clears factory's buffer to use materials for production"
  [buffer]
  (map #(assoc buffer (key %) 0) buffer)) ; just make all the materials' volumes equal to 0

(defn tryProduceFromBuffer
  "An utility method to check of factory's has enough materials to produce"
  ;; 'state' is for agent created in 'factory', see comments in its code for details
  [produce_amount duration target-storage bill buffer]
  (if
    (bufferMeetsBill buffer bill)
     (do
       (send printer print-msg (str "Enter tryProduce"))
       (Thread/sleep duration)
       (send (target-storage :worker) supply-msg produce_amount)
       (useBufferedMaterials buffer)) ; return only the empty buffer if production was successful
     buffer) ; return original buffer otherwise
   ) 

(defn notify-msg
  "A message that can be sent to a factory worker to notify that the provided 'amount' of 'ware's are
   just put to the 'storage-atom'."
   ;; 'state' is for agent created in 'factory', see comments in its code for details
   ;;The implementation should:
   ;; - try to retrieve some items from the 'storage-atom' if necessary - OK
   ;; - if the retrieval is not successful, do not forget to handle validation exception correctly - OK
   ;; - if the retrieval is successful, put wares into the internal ':buffer' - OK
   ;; - when there are enough wares of all types according to :bill, a new cycle must be started with given duratin;
   ;;   after it finished all the wares must be removed from the internal ':buffer' and ':target-storage' must be notified  
   ;;   with 'supply-msg'
   ;; - return new agent state with possibly modified ':buffer' in any case!
  [worker ware storage-atom amount]
  (let [produce_amount (worker :amount)
        duration (worker :duration)
        target-storage (worker :target-storage)
        bill (worker :bill)
        buffer (worker :buffer)]
    (send printer print-msg (str "NOTIFY STARTED\n storage = " (deref storage-atom)))
    (if
     (= @storage-atom 0) worker ; if all materials are already taken - do nothing; return old agent

      ; otherwise take some material
     (->>
      ; if buffer or bill is null print EUGH
      (let [stored (get buffer ware) ; amount in factory's hands
            billed (get bill ware)   ; amount required by the bill
            needed (- billed stored)]; amount needed to meet the bill in current state
        (try
          (swap! storage-atom #(- % needed)) ; try taking only needed amount of material
          (send printer print-msg (str (.format (new java.text.SimpleDateFormat "hh.mm.ss.SSS") (new java.util.Date)) " |> " ware " got needed amount: " needed))
          (send printer print-msg (str ware " left in storage: " @storage-atom))
          (assoc buffer ware billed) ; and placing them in the buffer so the bill is perfectly met
          (catch IllegalStateException _ (let [taken @storage-atom] ; exception is thrown if storage does not have enough material to meet the bill
                                           (send printer print-msg (str ware " left in storage: " @storage-atom))
                                           (swap! storage-atom #(- % taken)) ; otherwise take all the materials (not (reset! 0) to avoid clearing after it's been updated elsewehere)
                                           (send printer print-msg (str (.format (new java.text.SimpleDateFormat "hh.mm.ss.SSS") (new java.util.Date)) " |> " ware " got all amount: " taken))
                                           (send printer print-msg (str  "Now no " ware " in storage: " @storage-atom))
                                           (assoc buffer ware (+ stored taken)))))) ; and place all of them in buffer
      (tryProduceFromBuffer produce_amount duration target-storage bill)
      (assoc worker :buffer))))) ; return worker but with empty buffer if smth was produced

;;;
(def safe-storage (storage "Safe" 1))
(def safe-factory (factory 1 3000 safe-storage "Metal" 3))
(def cuckoo-clock-storage (storage "Cuckoo-clock" 1))
(def cuckoo-clock-factory (factory 1 2000 cuckoo-clock-storage "Lumber" 5 "Gears" 10))
(def gears-storage (storage "Gears" 20 cuckoo-clock-factory))
(def gears-factory (factory 4 1000 gears-storage "Ore" 4))
(def metal-storage (storage "Metal" 5 safe-factory))
(def metal-factory (factory 1 1000 metal-storage "Ore" 10))
(def lumber-storage (storage "Lumber" 20 cuckoo-clock-factory))
(def lumber-mill (source 5 4000 lumber-storage))
(def ore-storage (storage "Ore" 10 metal-factory gears-factory))
(def ore-mine (source 2 1000 ore-storage))

;;;runs sources and the whole process as the result
(defn start []
  (.start ore-mine)
  (.start lumber-mill))

;;;stopes running process
;;;recomplile the code after it to reset all the process
(defn stop []
  (.stop ore-mine)
  (.stop lumber-mill))

(defn -main
  []
  (println "start") ; последняя идея - сделать recur функции с попыткой swap! пока не перестанет кидаться исключение
  (start))

;;;This could be used to aquire errors from workers
;;;(agent-error (gears-factory :worker))
;;;(agent-error (metal-storage :worker))
