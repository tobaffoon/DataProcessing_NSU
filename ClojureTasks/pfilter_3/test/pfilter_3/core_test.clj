(ns pfilter-3.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [pfilter-3.core :refer [p-filter-infinite_lazy]]))

(deftest finite-test
  (testing "Finite Sequence Filtering"
    (is (= (filter even? (range 1 1000)) (p-filter-infinite_lazy even? (range 1 1000))))))

(deftest lazy-test
  (testing "Laziness"
    (is (= (take 2 (filter even? (range))) (take 2 (p-filter-infinite_lazy even? (range)))))))