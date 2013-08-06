(ns tray.core
  (:require [clojure.java.io :as io])
  (:import
	[javax.swing UIManager SwingUtilities JOptionPane ImageIcon]
    [java.awt SystemTray TrayIcon PopupMenu Menu MenuItem]
    [java.awt.event ActionListener]
    [java.net URL]))

(defn show []
 (let [pop (PopupMenu.)
       icon (TrayIcon. (.getImage (ImageIcon. (io/as-url (io/resource "bulb.gif")) "icon")))
       tray (SystemTray/getSystemTray)
       ;;menu (Menu.)
       item (MenuItem. "About")]
    (.add pop item)
    (.setPopupMenu icon pop)
    (.add tray icon)
    (.addActionListener icon 
      (reify ActionListener 
         (actionPerformed [this _] (JOptionPane/showMessageDialog nil "Hello Tray"))))))

(defn -main [& args] 
    ;;(UIManager/setLookAndFeel "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
    ;;(UIManager/put "swing.boldMetal" false)
    (SwingUtilities/invokeLater show))
