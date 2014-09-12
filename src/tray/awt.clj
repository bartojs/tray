(ns awt (:import [java.awt Frame Label SystemTray TrayIcon PopupMenu MenuItem] 
                 [java.awt.event WindowAdapter WindowEvent ActionListener] 
                 [java.awt.dnd DropTarget DropTargetListener DnDConstants]
                 [java.awt.datatransfer DataFlavor]
                 [javax.swing ImageIcon])
        (:require [clojure.java.io :as io]))

(def label (Label. "Hello world"))
(def frame (doto (Frame. "Hello") (.add label) (.setSize 300 100) (.addWindowListener (proxy [WindowAdapter] [] (windowClosing [event] (.setVisible (.getWindow event) false))))))
(def tray (SystemTray/getSystemTray))
(def icon (TrayIcon. (.getImage (ImageIcon. (io/as-url (io/resource "bulb.gif"))))))

(def dnd (DropTarget. frame (reify DropTargetListener 
                              (dragEnter [this _])
                              (dragExit [this _])
                              (dragOver [this _])
                              (dropActionChanged [this _])
                              (drop [this event] 
                                (.acceptDrop event DnDConstants/ACTION_LINK)
                                (.setText label (str "dropped " (.. event (getTransferable) (getTransferData DataFlavor/stringFlavor))))))))

(def popup (doto (PopupMenu.) 
             (.add (doto (MenuItem. "Open") (.addActionListener (reify ActionListener (actionPerformed [this _ ] (.setVisible frame true))))))
             (.add (doto (MenuItem. "Exit") (.addActionListener (reify ActionListener (actionPerformed [this _ ] (.remove tray icon) (System/exit 0)))))))) 

(.setPopupMenu icon popup)
(.add tray icon)
