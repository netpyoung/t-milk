(ns js.math
  "Wrapper for JavaScript Math functions.")

(def E
  "Euler's number."
 (.-E js/Math))

(def PI
  "The ratio of the circumference of a circle to its diameter."
  (.-PI js/Math))


(defn abs
  "#x {number}
   #return {number}: the absolute value of x."
  [x]
  (.abs js/Math x))

(defn ceil
  "#x {number}
   #return {number}: rounded upwards to the nearest integer."
  [x]
  (.ceil js/Math x))

(defn floor
  "#x {number}
   #return {number}: rounded downwards to the nearest integer."
  [x]
  (.floor js/Math x))

(defn round
  "#x {number}
   #return {number}: rounded to the nearest integer."
  [x]
  (.round js/Math x))


(defn sqrt
  "#x {number}: in radians
   #return {number}: the square root of x."
  [x]
  (.sqrt js/Math x))

(defn pow
  "#x, y {number}
   #return {number}: the value of x to the power of y."
  [x y]
  (.pow js/Math x y))

(defn exp
  "#x {number}
   #return {number}: the value of E^x."
  [x]
  (.exp js/Math x))

(defn log
  "#x {number}
   #return {number}: the natural logarithm (base E) of x."
  [x]

  (.log js/Math x))

(defn log10
  "#x {number}
   #return {number}: the logarithm (base 10) of x."
  [x]
  (/ (log x) (log 10)))


(defn sin
  "#x {number}: in radians
   #return {number}: the sine of x."
  [x]
  (.sin js/Math x))

(defn cos
  "#x {number}: in radians
   #return {number}: the cosine of x."
  [x]
  (.cos js/Math x))

(defn tan
  "#x {number}: in radians
   #return {number}: the tangent of x"
  [x]
  (.tan js/Math x))

(defn asin
  "#x {number}
   #return {number}: in radians, the arcsine of x."
  [x]
  (.asin js/Math x))

(defn acos
  "#x {number}
   #return {number}: in radians, the arccosine of x."
  [x]
  (.acos js/Math x))

(defn atan
  "#x {number}
   #return {number}: in radians, the arctangent of x."
  [x]
  (.atan js/Math x))

(defn atan2
  "#y, x {number}
   #return {number}: in radians, the arctangent of the quotient of its arguments."
  [y x]
  (.atan2 js/Math y x))


(defn deg->rad
  "#x {number}: in degrees
   #return {number}: in radians"
  [x]
  (* x (/ PI 180)))

(defn deg
  "#x {number}: in degrees
   #return {number}: in radians"
  [x]
  (deg->rad x))

(defn rad->deg
  "#x {number}: in radians
   #return {number}: in degrees"
  [x]
  (* x (/ 180 PI)))


