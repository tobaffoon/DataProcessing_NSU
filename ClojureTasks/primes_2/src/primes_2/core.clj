(ns primes-2.core
  (:gen-class))

(defn sieve [rest-naturals primes]
  (let [fst (first rest-naturals)] ; fst - первое из проверяемых чисел (потенциально простое)
    (if (every? #(not= 0 (mod fst %)) primes) ; Если на каждоге найденное простое число, fst не делится нацело 
      (lazy-seq ; то fst - простое число, так что выдаём на выход ленивую последовательность из него 
       (cons fst (sieve (rest rest-naturals) (conj primes fst)))) ; и из далее простых чисел, простота которых проверяется с учётом fst
      (recur (rest rest-naturals) primes)))) ; иначе - возвращаем дальнейшие простые числа, только теперь fst не рассматриваем вообще

; iterate inc 2 - ленивый список 2, 3, 4...
(def primes (sieve (iterate inc 2) []))

(defn -main
  []
  (println (take 2000 primes)))

