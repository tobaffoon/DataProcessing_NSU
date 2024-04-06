(->>
   (let [stored (get buffer ware) ; amount in factory's hands
         billed (get bill ware)   ; amount required by the bill
         needed (- billed stored)]; amount needed to meet the bill in current state
     (try
       (swap! storage-atom #(do ; try taking all the needed materials
                              (assoc buffer ware billed) ; placing them in the buffer so the bill is perfectly met
                              (- % needed))) ; subtract from storage
       (catch IllegalStateException ignored (swap! storage-atom (fn [value]  ; otherwise take all the materials
                                                                  (assoc buffer ware (+ stored value))
                                                                  0))))) ; and palce all of them in buffer
   )