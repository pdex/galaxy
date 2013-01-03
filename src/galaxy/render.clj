(ns galaxy.render
  (:import
    (java.awt Color Graphics Dimension)
    (java.awt.image BufferedImage)
    (javax.swing JPanel JFrame)))

(def device (. (java.awt.GraphicsEnvironment/getLocalGraphicsEnvironment) getDefaultScreenDevice) )

(defn fullscreen [device window f]
  (try (. device setFullScreenWindow window)
  (finally (. device setFullScreenWindow nil) ) ) )

(def mywin (proxy [JFrame]  []
  (paint [g] (. g drawString "Hello World!", 20, 50))))

(defn show [win x y] (doto win (.setSize x y) (.setVisible true)))

(def width 500)

(def height 500)

(defn render-star [bg star]
  (let [x (:x (:point star)) y (:y (:point star))]
  (. bg fillRect x y 1 1)))

(defn render-stars [bg color stars]
  (. bg setColor color)
  (dorun (map #(render-star bg %) stars)))

(defn render-galaxies [bg])

(defn render [g stars] 
  (let [img (new BufferedImage width height (. BufferedImage TYPE_INT_RGB))
        bg (. img (getGraphics))
        red (. Color red)
        blue (. Color blue)]
       (doto bg
         (.setColor (. Color black))
         (.fillRect 0 0 width height))
       (render-stars bg Color/red stars)
;       (dorun
;          (map (fn [ps c]
;              (println c)
;              (. bg setColor c) 
;              (dorun (map (fn [p] (comment println p)(. bg fillRect (:x p) (:y p) 1 1)) ps)))
;          points colors))
;       )
       (. g (drawImage img 0 0 nil))
  )
)

(defn build-frame [render]
  (let [panel (doto (proxy [JPanel] [] (paint [g] (render g)))
                (.setPreferredSize (new Dimension width height)))
        frame (doto (new JFrame) (.add panel) .pack .show)]
    frame))

(defn build-render [universe]
  (fn [g] (render g (:stars (first @(:galaxies universe))))))
