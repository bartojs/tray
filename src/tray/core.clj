(ns tray.core
  (:require [clojure.java.io :as io])
  (:import [javax.swing UIManager SwingUtilities ImageIcon JOptionPane]
    [java.awt SystemTray TrayIcon PopupMenu Menu MenuItem CheckboxMenuItem]
    [java.awt.event ActionListener ItemListener ItemEvent]))

(def current-item (atom nil))
(def checkboxes (atom {"tech support" nil "kayako" nil "retail" nil}))
(defn add-checkbox [item]
   (let [cb (doto (CheckboxMenuItem. item) 
             (.addItemListener (reify ItemListener 
                                 (itemStateChanged [this event]
                                  (let [selected (.getItemSelectable event)]
                                  (if (= (.getStateChange event) ItemEvent/SELECTED)
                                    (do (swap! current-item (constantly (.getItem event)))
                                      (println "change" (java.util.Date.) (.getLabel selected))) 
                                    (.setState selected true))
                                  (doseq [cx (vals @checkboxes)] 
                                    (when-not (identical? selected cx) 
                                                            (.setState cx false))))))))]
     (swap! checkboxes conj [item cb])
     cb))

(defn remove-checkbox [item]
   (swap! checkboxes dissoc item))

(defn update-checkbox [item]
  (when item
   (let [cb (get @checkboxes @current-item)] 
    (println "edit" (java.util.Date.) item) 
    (.setLabel cb item)
    (swap! checkboxes dissoc @current-item)
    (swap! checkboxes assoc item cb)
    (swap! current-item (constantly item)))))

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
            edititem (doto (MenuItem. "Edit") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _]
                       (when @current-item
                       (update-checkbox (JOptionPane/showInputDialog nil "Edit" "Edit" JOptionPane/PLAIN_MESSAGE nil nil @current-item))))))) 
            removeitem (doto (MenuItem. "Remove") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _] (when @current-item 
                                                                      (.remove popup (get @checkboxes @current-item)) 
                                                                      (remove-checkbox @current-item))))))
            popup (doto popup (.add additem) (.add edititem) (.add removeitem) (.add exititem) (.addSeparator))]
        (doseq [item (keys @checkboxes)] (.add popup (add-checkbox item))) 
        (.setPopupMenu icon popup)
        (.add tray icon)))))
