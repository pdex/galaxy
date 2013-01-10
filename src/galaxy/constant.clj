(ns galaxy.constant
  (:import (java.awt Color)))

; mininum number of galaxies
(def MINGALAXIES 2)
; maximum number of galaxies
(def MAXGALAXIES 5)
; available color palette
(def COLOR-PALETTE [
  Color/red
  Color/green
  Color/blue
  ])
; minimum number of stars in a galaxy
(def MIN-STARS 1000)
; maximum number of stars in a galaxy
(def MAX-STARS 3000)
; not sure
(def MAXRAND 2147483648.0)
(def DELTAT (* 50 0.0001))
(def GALAXYRANGESIZE 0.1)
(def GALAXYMINSIZE 0.15)
(def QCONS 0.001)
(def EPSILON 0.00000001)
(def sqrt_EPSILON 0.0001)
(def eps (let [e (/ 1 (* EPSILON sqrt_EPSILON DELTAT DELTAT QCONS))] (* e (Math/sqrt e))))
(def M_PI 3.1415926535)
