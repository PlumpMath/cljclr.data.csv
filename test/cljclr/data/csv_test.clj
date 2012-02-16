(ns cljclr.data.csv-test                                                  ;RR: clojure.data.csv-test
  (:use
   [clojure.test :only (deftest is)]
   [cljclr.data.csv :only (read-csv write-csv)])
  (:import
   [System.IO TextReader TextWriter StringWriter EndOfStreamException]))  ;RR:  [java.io Reader Writer StringReader EOFException]


(def ^{:private true} simple
  "Year,Make,Model
1997,Ford,E350
2000,Mercury,Cougar
")

(def ^{:private true} simple-alt-sep
  "Year;Make;Model
1997;Ford;E350
2000;Mercury;Cougar
")

(def ^{:private true} complicated
  "1997,Ford,E350,\"ac, abs, moon\",3000.00
1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00
1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",\"\",5000.00
1996,Jeep,Grand Cherokee,\"MUST SELL!
air, moon roof, loaded\",4799.00")

(deftest reading
  (let [csv (read-csv simple)]
    (is (= (count csv) 3))
    (is (= (count (first csv)) 3))
    (is (= (first csv) ["Year" "Make" "Model"]))
    (is (= (last csv) ["2000" "Mercury" "Cougar"])))
  (let [csv (read-csv simple-alt-sep :separator \;)]
    (is (= (count csv) 3))
    (is (= (count (first csv)) 3))
    (is (= (first csv) ["Year" "Make" "Model"]))
    (is (= (last csv) ["2000" "Mercury" "Cougar"])))
  (let [csv (read-csv complicated)]
    (is (= (count csv) 4))
    (is (= (count (first csv)) 5))
    (is (= (first csv)
           ["1997" "Ford" "E350" "ac, abs, moon" "3000.00"]))
    (is (= (last csv)
           ["1996" "Jeep" "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded" "4799.00"]))))
        

(deftest reading-and-writing
  (let [string-writer (StringWriter.)]
    (->> simple read-csv (write-csv string-writer))
    (is (= simple
	   (str string-writer)))))

(deftest throw-if-quoted-on-eof
  (let [s "ab,\"de,gh\nij,kl,mn"]
    (is (thrown? Exception (doall (read-csv s))))))

