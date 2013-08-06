(ns tray.core
  (:require [clojure.java.io :as io])
  (:import [javax.swing UIManager SwingUtilities JOptionPane ImageIcon]
    [java.awt SystemTray TrayIcon PopupMenu Menu MenuItem]
    [java.awt.event ActionListener ItemListener]
    [java.net URL]))

(defn -main [& args] 
  (SwingUtilities/invokeLater 
    (fn []
      (let [icon (TrayIcon. (.getImage (ImageIcon. (io/as-url (io/resource "bulb.gif")) "icon")))
            item (doto (MenuItem. "Exit") 
                   (.addActionListener 
                    (reify ActionListener (actionPerformed [this _] (.remove (SystemTray/getSystemTray) icon) (System/exit 0)))))
            popup (doto (PopupMenu.) (.add item))]
        (.setPopupMenu icon popup)
        (.add (SystemTray/getSystemTray) icon)))))
