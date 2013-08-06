(ns tray.core
  (:require [clojure.java.io :as io])
  (:import [javax.swing UIManager SwingUtilities ImageIcon]
    [java.awt SystemTray TrayIcon PopupMenu Menu MenuItem CheckboxMenuItem]
    [java.awt.event ActionListener ItemListener]))

(def current-item (atom nil))
(def items (atom ["tech support" "kayako" "retail"]))
(def checkboxes (atom []))
(defn add-checkbox [item]
   (let [cb (doto (CheckboxMenuItem. item) 
             (.addItemListener (reify ItemListener 
                                 (itemStateChanged [this event]
                                   (println "statechange" (.getLabel (.getItemSelectable event)))
                                  (doseq [cx @checkboxes] 
                                    (println "off" (.getLabel cx))
                                    (when-not (identical? (.getItemSelectable event) cx) 
                                                            (.setState cx false)))))))]
     (swap! checkboxes conj cb)
     cb))

(defn -main [& args] 
  (SwingUtilities/invokeLater 
    (fn []
      (let [tray (SystemTray/getSystemTray)
            icon (TrayIcon. (.getImage (ImageIcon. (io/as-url (io/resource "bulb.gif")) "icon")))
            exititem (doto (MenuItem. "Exit") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _] (.remove tray icon) (System/exit 0)))))
            popup (doto (PopupMenu.) (.add exititem) (.addSeparator))]
        (doseq [item @items] (.add popup (add-checkbox item))) 
        (.setPopupMenu icon popup)
        (.add tray icon)))))
