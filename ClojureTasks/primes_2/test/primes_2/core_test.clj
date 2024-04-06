(ns primes-2.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [primes-2.core :refer [primes]]))

(deftest first-test
  (testing "First Prime"
    (is (== (nth primes 0) 2))))

(deftest second-test
  (testing "Seconds Prime"
    (is (== (nth primes 1) 3))))

(deftest thousand-test
  (testing "1000th Prime"
    (is (== (nth primes 999) 7919))))

(deftest four-thousand-and-first-test
  (testing "4001st Prime"
    (is (== (nth primes 4000) 37831))))
