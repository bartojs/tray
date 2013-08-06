(ns tray.core
  (:require [clojure.java.io :as io])
  (:import [javax.swing UIManager SwingUtilities ImageIcon]
    [java.awt SystemTray TrayIcon PopupMenu Menu MenuItem CheckboxMenuItem]
    [java.awt.event ActionListener ItemListener ItemEvent]))

(def current-item (atom nil))
(def checkboxes (atom {"tech support" nil "kayako" nil "retail" nil}))
(defn add-checkbox [item]
   (let [cb (doto (CheckboxMenuItem. item) 
             (.addItemListener (reify ItemListener 
                                 (itemStateChanged [this event]
                                   (println "statechange" (.getLabel (.getItemSelectable event)))
                                  (if (= (.getStateChange event) ItemEvent/SELECTED)
                                    (swap! current-item (constantly  (.getItemSelectable event))) 
                                    (.setState (.getItemSelectable event) true))
                                  (doseq [cx (vals @checkboxes)] 
                                    (println "off" (.getLabel cx))
                                    (when-not (identical? (.getItemSelectable event) cx) 
                                                            (.setState cx false)))))))]
     (swap! checkboxes conj [item cb])
     cb))

(defn -main [& args] 
  (SwingUtilities/invokeLater 
    (fn []
      (let [tray (SystemTray/getSystemTray)
            icon (TrayIcon. (.getImage (ImageIcon. (io/as-url (io/resource "bulb.gif")) "icon")))
            popup (PopupMenu.)
            exititem (doto (MenuItem. "Exit") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _] (.remove tray icon) (System/exit 0)))))
            additem (doto (MenuItem. "Add") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _] (.add popup (add-checkbox "new"))))))
            removeitem (doto (MenuItem. "Remove") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _] (when @current-item (.remove popup @current-item) 
                                                                      (swap! checkboxes dissoc (.getLabel @current-item)))))))
            popup (doto popup (.add additem) (.add removeitem) (.add exititem) (.addSeparator))]
        (doseq [item (keys @checkboxes)] (.add popup (add-checkbox item))) 
        (.setPopupMenu icon popup)
        (.add tray icon)))))
