(ns galaxy.core
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
(def random (java.util.Random.))
(def M_PI 3.1415926535)
(defn LRAND [] (bit-and (. random nextLong) 0x7fffffff))
(defn NRAND [n] (mod (LRAND) n))
(def MAXRAND 2147483648.0)
(defn FLOATRAND [] (/ (LRAND) MAXRAND))
(def DELTAT (* 50 0.0001))
(def GALAXYRANGESIZE 0.1)
(def GALAXYMINSIZE 0.15)
(def QCONS 0.001)
;#define LRAND()         ((long) (random() & 0x7fffffff))
;#define NRAND(n)        ((int) (LRAND() % (n)))
;#define MAXRAND         (2147483648.0) /* unsigned 1<<31 as a float */
;#define FLOATRAND ((double) LRAND() / ((double) MAXRAND))
;#define MAX_IDELTAT    50
;#define DELTAT (MAX_IDELTAT * 0.0001)
;#define GALAXYRANGESIZE  0.1
;#define GALAXYMINSIZE  0.15
(defn randomColor [] (. random nextInt))
(defn point [x y] { :x x :y y })
(defn star [pos vel] { :pos pos :vel vel })
(defn galaxy [mass nstars stars oldpoints newpoints pos vel color] {
    :mass mass
    :nstars nstars
    :stars stars
    :oldpoints oldpoints
    :newpoints newpoints
    :pos pos
    :vel vel
    :color color
    })
(defn universe [mat scale midx midy size diff galaxies ngalaxies hititerations step rot_y rot_x] {
    :mat mat
    :scale scale
    :midx midx
    :midy midy
    :size size
    :diff diff
    :galaxies galaxies
    :ngalaxies ngalaxies
    :hititerations hititerations
    :step step
    :rot_y rot_y
    :rot_x rot_x
    })
(defn build-star [pos vel size mass mat]
    (let [
          w (* 2.0 M_PI (FLOATRAND))
          sinw (Math/sin w)
          cosw (Math/cos w)
          d (* (FLOATRAND) size)
;   h = FLOATRAND * exp(-2.0 * (d / gp->size)) / 5.0 * gp->size;
;   if (FLOATRAND < 0.5)
;    h = -h;
          h ((fn [x] (if (< (FLOATRAND) 0.5) (- x) x))
             (* (FLOATRAND) (/ (Math/exp (* -2.0 (/ d size))) 5.0) size))
;   v = sqrt(gt->mass * QCONS / sqrt(d * d + h * h));
          v (Math/sqrt (/ (* mass QCONS) (Math/sqrt (+ (* d d) (* h h)))))
         ]
    (star
;   st->pos[0] = gp->mat[0][0] * d * cosw + gp->mat[1][0] * d * sinw + gp->mat[2][0] * h + gt->pos[0];
;   st->pos[1] = gp->mat[0][1] * d * cosw + gp->mat[1][1] * d * sinw + gp->mat[2][1] * h + gt->pos[1];
;   st->pos[2] = gp->mat[0][2] * d * cosw + gp->mat[1][2] * d * sinw + gp->mat[2][2] * h + gt->pos[2];
        (apply vector
            (map (fn [x y z pos] (+ (* x d cosw) (* y d sinw) (* z h) pos))
                (first mat) (second mat) (nth mat 2) pos)) ;pos
;   st->vel[0] = -gp->mat[0][0] * v * sinw + gp->mat[1][0] * v * cosw + gt->vel[0];
;   st->vel[1] = -gp->mat[0][1] * v * sinw + gp->mat[1][1] * v * cosw + gt->vel[1];
;   st->vel[2] = -gp->mat[0][2] * v * sinw + gp->mat[1][2] * v * cosw + gt->vel[2];
;
;   st->vel[0] *= DELTAT;
;   st->vel[1] *= DELTAT;
;   st->vel[2] *= DELTAT;
        (apply vector
            (map (fn [x y vel] (* DELTAT (+ (* (- x) v sinw) (* y v cosw) vel)))
                (first mat) (second mat) vel)) ;vel
    )
    ))
(defn build-vector [n init]
    (apply vector (map (fn [_] (init)) (range n))))
(defn build-galaxy [n fhit size mat]
    (let [vel (build-vector 3 #(- (* (FLOATRAND) 2.0) 1.0))
          pos (apply vector (map (fn [v] (- (+ (* (- v) DELTAT fhit) (FLOATRAND) ) 0.5)) vel))
          mass (int (+ (* (FLOATRAND) 1000.0) 1))
         ]
    (galaxy
        mass ;mass
        n ;nstars
        (build-vector n #(build-star pos vel size mass mat)) ;stars
        (build-vector n #(point 0 0)) ;oldpoints
        (build-vector n #(point 0 0)) ;newpoints
        pos ;pos
        vel ;vel
        0 ;XXX;color
;  gt->galcol = NRAND(COLORBASE - 2);
;  if (gt->galcol > 1)
;   gt->galcol += 2; /* Mult 8; 16..31 no green stars */
;  /* Galaxies still may have some green stars but are not all green. */
    )))
(defn build-mat [w1 w2]
    (let [sinw1 (Math/sin w1)
          sinw2 (Math/sin w2)
          cosw1 (Math/cos w1)
          cosw2 (Math/cos w2)
         ]
    (list
      [cosw2 (* (- sinw1) sinw2) (* cosw1 sinw2)]
      [0.0 cosw1 sinw1]
      [(- sinw2) (* (- sinw1) cosw2) (* cosw1 cosw2)])
    ))
(defn startover [universe]
    (let [
          w1 (* 2.0 M_PI (FLOATRAND))
          w2 (* 2.0 M_PI (FLOATRAND))
          mat (build-mat w1 w2)
          size (+ GALAXYMINSIZE (* GALAXYRANGESIZE (FLOATRAND)))
          g (build-vector 2 #(build-galaxy 100 (:hititerations universe) size mat))  
         ]
    (assoc universe
        :step 0
        :rot_y 0.0
        :rot_x 0.0
        :galaxies g
        :ngalaxies (count g)
        :mat mat 
        :size (+ GALAXYMINSIZE (* GALAXYRANGESIZE (FLOATRAND)))
        :size size
    )))
(defn init-galaxy [width height cycles]
    (assoc
    (startover
        (universe
        '([0.0 0.0 0.0]
          [0.0 0.0 0.0]
          [0.0 0.0 0.0]) ;mat
        (/ (+ width height) 8.0) ;scale
; gp->scale = (double) (MI_WIN_WIDTH(mi) + MI_WIN_HEIGHT(mi)) / 8.0;
        (/ width 2) ;midx
; gp->midx =  MI_WIN_WIDTH(mi)  / 2;
        (/ height 2) ;midy
; gp->midy =  MI_WIN_HEIGHT(mi) / 2;
        0.0 ;size
        [0.0 0.0 0.0] ;diff
        [ ] ;galaxies
        0 ;ngalaxies
        cycles ;hititerations
; gp->f_hititerations = MI_CYCLES(mi);
        0 ;step
        0.0 ;rot_y
        0.0 ;rot_x
        )
    )
    :width width
    :height height))
(defn project-stars [galaxy x y scale mx my]
    (let [cox (Math/cos y) six (Math/sin y) cor (Math/cos x) sir (Math/sin x)]
;      newp->x = (short) (((cox * st->pos[0]) - (six * st->pos[2])) * gp->scale) + gp->midx;
;      newp->y = (short) (((cor * st->pos[1]) - (sir * ((six * st->pos[0]) + (cox * st->pos[2]))))* gp->scale) + gp->midy;
        (map (fn [star]
            (let [pos (:pos star)] (point
                (short (+ (* (- (* cox (first pos)) (* six (nth pos 2))) scale) mx));x
                (short (+ (* (- (* cor (second pos)) (* sir (+ (* six (first pos)) (* cox (nth pos 2))))) scale) my));y
            ))) (:stars galaxy))
        ;(:newpoints galaxy)
        ;(assoc galaxy :newpoints (:newpoints galaxy))
    ))
(defn render [g w h gpoints] 
    (let [
          img (new BufferedImage w h (. BufferedImage TYPE_INT_RGB))
          bg (. img (getGraphics)) 
          red (. Color red)
          blue (. Color blue)
         ] 
         (doto bg
           (.setColor (. Color black))
           (.fillRect 0 0 w h))
         (dorun
            (map (fn [ps c] (println c) (. bg setColor c) 
                (dorun (map (fn [p] (println p)(. bg fillRect (:x p) (:y p) 1 1)) ps)))
            gpoints [red blue]))
         (. g (drawImage img 0 0 nil))
         ))
(defn draw-universe [universe]
    (let [f (fn [galaxy] (apply project-stars galaxy (map universe '(:rot_x :rot_y :scale :midx :midy))))
          width (:width universe)
          height (:height universe)
          gpoints (map f (:galaxies universe))
          panel (doto (proxy [JPanel] []
                            (paint [g] (render g width height gpoints)))
                 (.setPreferredSize (new Dimension width height)))
          frame (doto (new JFrame) (.add panel) .pack .show)
        ]
    ))

;#ifdef STANDALONE
;# define DEFAULTS	"*delay:  20000  \n"   \
;					"*count:  -5     \n"   \
;					"*cycles:  250   \n"   \
;					"*ncolors:  64   \n" \
;					"*fpsSolid:  true   \n" \
;
;# define UNIFORM_COLORS
;# define reshape_galaxy 0
;# define galaxy_handle_event 0
;# include "xlockmore.h"    /* from the xscreensaver distribution */
;#else  /* !STANDALONE */
;# include "xlock.h"     /* from the xlockmore distribution */
;#endif /* !STANDALONE */
;
;static Bool tracks;
;static Bool spin;
;static Bool dbufp;
;
;#define DEF_TRACKS "True"
;#define DEF_SPIN   "True"
;#define DEF_DBUF   "True"
;
;static XrmOptionDescRec opts[] =
;{
; {"-tracks", ".galaxy.tracks", XrmoptionNoArg, "on"},
; {"+tracks", ".galaxy.tracks", XrmoptionNoArg, "off"},
; {"-spin",   ".galaxy.spin",   XrmoptionNoArg, "on"},
; {"+spin",   ".galaxy.spin",   XrmoptionNoArg, "off"},
; {"-dbuf",   ".galaxy.dbuf",   XrmoptionNoArg, "on"},
; {"+dbuf",   ".galaxy.dbuf",   XrmoptionNoArg, "off"},
;};
;
;static argtype vars[] =
;{
; {&tracks, "tracks", "Tracks", DEF_TRACKS, t_Bool},
; {&spin,   "spin",   "Spin",   DEF_SPIN,   t_Bool},
; {&dbufp,  "dbuf",   "Dbuf",   DEF_DBUF,   t_Bool}, 
;};
;
;static OptionStruct desc[] =
;{
; {"-/+tracks", "turn on/off star tracks"},
; {"-/+spin",   "do/don't spin viewpoint"},
; {"-/+dbuf",   "turn on/off double buffering."},
;};
;
;ENTRYPOINT ModeSpecOpt galaxy_opts =
;{sizeof opts / sizeof opts[0], opts,
; sizeof vars / sizeof vars[0], vars, desc};
;
;
;#define FLOATRAND ((double) LRAND() / ((double) MAXRAND))
;
;#if 0
;#define WRAP       1  /* Warp around edges */
;#define BOUNCE     1  /* Bounce from borders */
;#endif
;
;#define MINSIZE       1
;#define MINGALAXIES    2
;#define MAX_STARS    3000
;#define MAX_IDELTAT    50
;/* These come originally from the Cluster-version */
;#define DEFAULT_GALAXIES  3
;#define DEFAULT_STARS    1000
;#define DEFAULT_HITITERATIONS  7500
;#define DEFAULT_IDELTAT    200 /* 0.02 */
;#define EPSILON 0.00000001
;
;#define sqrt_EPSILON 0.0001
;
;#define DELTAT (MAX_IDELTAT * 0.0001)
;
;#define GALAXYRANGESIZE  0.1
;#define GALAXYMINSIZE  0.15
;#define QCONS    0.001
;
;
;#define COLORBASE  16
;/* colors per galaxy */
;/* #define COLORSTEP  (NUMCOLORS/COLORBASE) */
;# define COLORSTEP (MI_NCOLORS(mi)/COLORBASE)
;
;
;ENTRYPOINT void
;draw_galaxy(ModeInfo * mi)
;{
;  Display    *display = MI_DISPLAY(mi);
;  Window      window = MI_WINDOW(mi);
;  GC          gc = MI_GC(mi);
;  unistruct  *gp = &universes[MI_SCREEN(mi)];
;  double      d, eps, cox, six, cor, sir;  /* tmp */
;  int         i, j, k; /* more tmp */
;  XPoint    *dummy = NULL;
;
;  if (! dbufp)
;    XClearWindow(MI_DISPLAY(mi), MI_WINDOW(mi));
;
;  if(spin){
;    gp->rot_y += 0.01;
;    gp->rot_x += 0.004;
;  }
;
;  cox = COSF(gp->rot_y);
;  six = SINF(gp->rot_y);
;  cor = COSF(gp->rot_x);
;  sir = SINF(gp->rot_x);
;
;  eps = 1/(EPSILON * sqrt_EPSILON * DELTAT * DELTAT * QCONS);
;
;  for (i = 0; i < gp->ngalaxies; ++i) {
;    Galaxy     *gt = &gp->galaxies[i];
;
;    for (j = 0; j < gp->galaxies[i].nstars; ++j) {
;      Star       *st = &gt->stars[j];
;      XPoint     *newp = &gt->newpoints[j];
;      double      v0 = st->vel[0];
;      double      v1 = st->vel[1];
;      double      v2 = st->vel[2];
;
;      for (k = 0; k < gp->ngalaxies; ++k) {
;        Galaxy     *gtk = &gp->galaxies[k];
;        double      d0 = gtk->pos[0] - st->pos[0];
;        double      d1 = gtk->pos[1] - st->pos[1];
;        double      d2 = gtk->pos[2] - st->pos[2];
;
;        d = d0 * d0 + d1 * d1 + d2 * d2;
;        if (d > EPSILON)
;          d = gtk->mass / (d * sqrt(d)) * DELTAT * DELTAT * QCONS;
;        else
;          d = gtk->mass / (eps * sqrt(eps));
;        v0 += d0 * d;
;        v1 += d1 * d;
;        v2 += d2 * d;
;      }
;
;      st->vel[0] = v0;
;      st->vel[1] = v1;
;      st->vel[2] = v2;
;
;      st->pos[0] += v0;
;      st->pos[1] += v1;
;      st->pos[2] += v2;
;
;      newp->x = (short) (((cox * st->pos[0]) - (six * st->pos[2])) *
;                         gp->scale) + gp->midx;
;      newp->y = (short) (((cor * st->pos[1]) - (sir * ((six * st->pos[0]) +
;                                                       (cox * st->pos[2]))))
;                         * gp->scale) + gp->midy;
;
;    }
;
;    for (k = i + 1; k < gp->ngalaxies; ++k) {
;      Galaxy     *gtk = &gp->galaxies[k];
;      double      d0 = gtk->pos[0] - gt->pos[0];
;      double      d1 = gtk->pos[1] - gt->pos[1];
;      double      d2 = gtk->pos[2] - gt->pos[2];
;
;      d = d0 * d0 + d1 * d1 + d2 * d2;
;      if (d > EPSILON)
;        d = 1 / (d * sqrt(d)) * DELTAT * QCONS;
;      else
;        d = 1 / (EPSILON * sqrt_EPSILON) * DELTAT * QCONS;
;
;      d0 *= d;
;      d1 *= d;
;      d2 *= d;
;      gt->vel[0] += d0 * gtk->mass;
;      gt->vel[1] += d1 * gtk->mass;
;      gt->vel[2] += d2 * gtk->mass;
;      gtk->vel[0] -= d0 * gt->mass;
;      gtk->vel[1] -= d1 * gt->mass;
;      gtk->vel[2] -= d2 * gt->mass;
;    }
;
;    gt->pos[0] += gt->vel[0] * DELTAT;
;    gt->pos[1] += gt->vel[1] * DELTAT;
;    gt->pos[2] += gt->vel[2] * DELTAT;
;
;    if (dbufp) {
;      XSetForeground(display, gc, MI_WIN_BLACK_PIXEL(mi));
;      XDrawPoints(display, window, gc, gt->oldpoints, gt->nstars,
;                  CoordModeOrigin);
;    }
;    XSetForeground(display, gc, MI_PIXEL(mi, COLORSTEP * gt->galcol));
;    XDrawPoints(display, window, gc, gt->newpoints, gt->nstars,
;                CoordModeOrigin);
;
;    dummy = gt->oldpoints;
;    gt->oldpoints = gt->newpoints;
;    gt->newpoints = dummy;
;  }
;
;  gp->step++;
;  if (gp->step > gp->f_hititerations * 4)
;    startover(mi);
;}
;
;ENTRYPOINT void
;release_galaxy(ModeInfo * mi)
;{
; if (universes != NULL) {
;  int         screen;
;
;  for (screen = 0; screen < MI_NUM_SCREENS(mi); screen++)
;   free_galaxies(&universes[screen]);
;  (void) free((void *) universes);
;  universes = NULL;
; }
;}
;
;ENTRYPOINT void
;refresh_galaxy(ModeInfo * mi)
;{
; /* Do nothing, it will refresh by itself */
;}
;
;XSCREENSAVER_MODULE ("Galaxy", galaxy)
;
