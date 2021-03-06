/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tinyrcp.example;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.tinyrcp.App;
import org.tinyrcp.JPluginsFrame;
import org.tinyrcp.JSettingsFrame;
import org.tinyrcp.JarClassLoader;
import org.tinyrcp.TinyFactory;
import org.tinyrcp.TinyPlugin;
import org.tinyrcp.tabs.JTabsPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Main frame for the application
 *
 * @author sbodmer
 */
public class JExample extends javax.swing.JFrame implements ActionListener {

    App app = null;

    /**
     * Current loaded config
     */
    File file = null;

    TinyPlugin main = null;
    JPluginsFrame jplugins = null;
    JSettingsFrame jsettings = null;

    /**
     * Currently open frames
     */
    ArrayList<JFrame> frames = new ArrayList<>();

    /**
     * Creates new form JWorldWindEarth
     */
    public JExample(App app) {
        this.app = app;

        initComponents();

        MN_New.addActionListener(this);
        MN_Load.addActionListener(this);
        MN_Save.addActionListener(this);
        MN_SaveAs.addActionListener(this);
        MN_Settings.addActionListener(this);

        MN_Exit.addActionListener(this);

        MN_Plugins.addActionListener(this);

        MN_Windows.add(app.createFactoryMenus("Panels", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_PANEL, this), 0);
        MN_Windows.add(app.createFactoryMenus("Containers", TinyFactory.PLUGIN_CATEGORY_PANEL, TinyFactory.PLUGIN_FAMILY_CONTAINER, this), 1);

        jplugins = new JPluginsFrame();
        jplugins.initialize(app);

        jsettings = new JSettingsFrame();
        jsettings.initialize(app);
    }

    //**************************************************************************
    //*** API
    //**************************************************************************
    public void open() {
        //--- Check the configuration file in user home dir
        file = new File(System.getProperty("user.home"), ".ExampleApp" + File.separator + "ExampleApp.xml");
        if (!file.exists()) {
            //--- Copy default configuration if not found
            try {
                file.getParentFile().mkdirs();
                FileOutputStream fout = new FileOutputStream(file);
                InputStream in = getClass().getResourceAsStream("/org/tinyrcp/example/Resources/Configs/ExampleApp.xml");
                byte buffer[] = new byte[4096];
                while (true) {
                    int read = in.read(buffer);
                    if (read == -1) break;
                    fout.write(buffer, 0, read);
                }
                fout.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //--- Load the configuration
        load(file);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("new")) {
            remove(main.getVisualComponent());
            main.cleanup();
            TinyFactory fac = app.getFactory("org.tinyrcp.tabs.JTabsFactory");
            main = fac.newPlugin(null);
            add(main.getVisualComponent(), BorderLayout.CENTER);
            main.setup(app, null);
            main.configure(null);
            repaint();

        } else if (e.getActionCommand().equals("newPlugin")) {

            String title = JOptionPane.showInputDialog(this, "Title");
            if (title == null) return;

            JMenuItem ji = (JMenuItem) e.getSource();
            TinyFactory factory = (TinyFactory) ji.getClientProperty("factory");
            TinyPlugin p = factory.newPlugin(null);
            p.setup(app, null);
            p.configure(null);
            JComponent jcomp = p.getVisualComponent();
            jcomp.putClientProperty("plugin", p);

            JFrame jframe = new JFrame(title);
            jframe.getContentPane().add(jcomp);
            jframe.setSize(640, 480);
            jframe.setLocationRelativeTo(this);
            jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            jframe.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    JFrame jframe = (JFrame) evt.getWindow();
                    int rep = JOptionPane.showConfirmDialog(jframe, "Do you really want to close this window ?");
                    if (rep == JOptionPane.YES_OPTION) {
                        JComponent jcomp = (JComponent) jframe.getContentPane().getComponent(0);
                        jframe.remove(jcomp);

                        TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
                        p.cleanup();

                        jframe.setVisible(false);
                        jframe.dispose();

                        frames.remove(jframe);
                    }
                }
            });
            jframe.setVisible(true);
            frames.add(jframe);

        } else if (e.getActionCommand().equals("load")) {
            JFileChooser jf = new JFileChooser(file);
            int rep = jf.showOpenDialog(this);
            if (rep == JFileChooser.APPROVE_OPTION) {
                file = jf.getSelectedFile();
                remove(main.getVisualComponent());
                main.cleanup();
                load(file);
            }

        } else if (e.getActionCommand().equals("save")) {
            save(file);

        } else if (e.getActionCommand().equals("saveAs")) {
            JFileChooser jf = new JFileChooser(file);
            int rep = jf.showSaveDialog(this);
            if (rep == JFileChooser.APPROVE_OPTION) {
                file = jf.getSelectedFile();
                save(file);
            }

        } else if (e.getActionCommand().equals("plugins")) {
            jplugins.setLocationRelativeTo(this);
            jplugins.setVisible(true);

        } else if (e.getActionCommand().equals("settings")) {
            jsettings.setLocationRelativeTo(this);
            jsettings.setVisible(true);

        } else if (e.getActionCommand().equals("exit")) {
            close();

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MB_Topbar = new javax.swing.JMenuBar();
        MN_File = new javax.swing.JMenu();
        MN_New = new javax.swing.JMenuItem();
        MN_Load = new javax.swing.JMenuItem();
        MN_Save = new javax.swing.JMenuItem();
        MN_SaveAs = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        MN_Settings = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        MN_Exit = new javax.swing.JMenuItem();
        MN_Tools = new javax.swing.JMenu();
        MN_Plugins = new javax.swing.JMenuItem();
        MN_Windows = new javax.swing.JMenu();
        MN_Help = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TinyRCP Example App");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        MN_File.setText("File");

        MN_New.setText("New");
        MN_New.setActionCommand("new");
        MN_File.add(MN_New);

        MN_Load.setText("Load configuration");
        MN_Load.setActionCommand("load");
        MN_File.add(MN_Load);

        MN_Save.setText("Save configuration");
        MN_Save.setActionCommand("save");
        MN_File.add(MN_Save);

        MN_SaveAs.setText("Save configuration as ...");
        MN_SaveAs.setActionCommand("saveAs");
        MN_File.add(MN_SaveAs);
        MN_File.add(jSeparator1);

        MN_Settings.setText("Settings");
        MN_Settings.setActionCommand("settings");
        MN_File.add(MN_Settings);
        MN_File.add(jSeparator2);

        MN_Exit.setText("Exit");
        MN_Exit.setActionCommand("exit");
        MN_File.add(MN_Exit);

        MB_Topbar.add(MN_File);

        MN_Tools.setText("Tools");

        MN_Plugins.setText("Plugins");
        MN_Plugins.setActionCommand("plugins");
        MN_Tools.add(MN_Plugins);

        MB_Topbar.add(MN_Tools);

        MN_Windows.setText("Windows");
        MB_Topbar.add(MN_Windows);

        MN_Help.setText("Help");
        MB_Topbar.add(MN_Help);

        setJMenuBar(MB_Topbar);

        setSize(new java.awt.Dimension(810, 630));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        close();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenuBar MB_Topbar;
    protected javax.swing.JMenuItem MN_Exit;
    protected javax.swing.JMenu MN_File;
    protected javax.swing.JMenu MN_Help;
    protected javax.swing.JMenuItem MN_Load;
    protected javax.swing.JMenuItem MN_New;
    protected javax.swing.JMenuItem MN_Plugins;
    protected javax.swing.JMenuItem MN_Save;
    protected javax.swing.JMenuItem MN_SaveAs;
    protected javax.swing.JMenuItem MN_Settings;
    protected javax.swing.JMenu MN_Tools;
    protected javax.swing.JMenu MN_Windows;
    protected javax.swing.JPopupMenu.Separator jSeparator1;
    protected javax.swing.JPopupMenu.Separator jSeparator2;
    // End of variables declaration//GEN-END:variables

    protected void save(File save) {
        try {
            //--- Create main config
            Document document = app.getDocumentBuilder().newDocument();
            Element root = document.createElement("ExampleApp");
            root.setAttribute("x", "" + getX());
            root.setAttribute("y", "" + getY());
            root.setAttribute("width", "" + getWidth());
            root.setAttribute("height", "" + getHeight());
            app.store(root);

            //--- Store the main GUI
            Element e = document.createElement("Main");
            main.saveConfig(e);
            root.appendChild(e);

            //--- Store the open frames
            for (int i = 0; i < frames.size(); i++) {
                JFrame jframe = frames.get(i);
                Element f = document.createElement("Frame");
                f.setAttribute("title", jframe.getTitle());
                f.setAttribute("x", "" + jframe.getX());
                f.setAttribute("y", "" + jframe.getY());
                f.setAttribute("width", "" + jframe.getWidth());
                f.setAttribute("height", "" + jframe.getHeight());

                JComponent jcomp = (JComponent) jframe.getContentPane().getComponent(0);
                TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
                Element c = document.createElement("Content");
                c.setAttribute("factory", p.getPluginFactory().getClass().getName());
                p.saveConfig(c);
                f.appendChild(c);

                root.appendChild(f);
            }
            
            document.appendChild(root);

            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer transformer = tfactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            FileOutputStream out = new FileOutputStream(save);
            save.getParentFile().mkdirs();
            // file.createNewFile();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
            out.close();

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Load the passed configuration file and create the components.<p>
     *
     * First the App singleton is configured with the passed configuration to
     * configure the factories<p>
     *
     *
     * @param load
     */
    protected void load(File load) {
        Element root = null;
        try {
            FileInputStream in = new FileInputStream(load);
            Document tmp = app.getDocumentBuilder().parse(in);

            root = tmp.getDocumentElement();
            in.close();

            //--- The key is the factory class name
            HashMap<String, Element> confs = new HashMap<>();
            Element mainNode = null;
            ArrayList<Element> windowConfs = new ArrayList<>();

            NodeList nl = root.getChildNodes();
            //--- Store the configuration nodes
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeName().equals("Main")) {
                    mainNode = (Element) nl.item(i);

                } else if (nl.item(i).getNodeName().equals("Factory")) {
                    Element e = (Element) nl.item(i);
                    confs.put(e.getAttribute("class"), e);

                } else if (nl.item(i).getNodeName().equals("Frame")) {
                    windowConfs.add((Element) nl.item(i));
                }
            }

            //--- Configure all the factories first
            ArrayList<TinyFactory> facs = app.getFactories(null);
            for (int i = 0; i < facs.size(); i++) {
                TinyFactory fac = facs.get(i);
                fac.configure(confs.get(fac.getClass().getName()));
            }

            //--- Find main panel, if not found use default tabs panel
            if (mainNode != null) {
                TinyFactory fac = app.getFactory(mainNode.getAttribute("factory"));
                if (fac != null) main = fac.newPlugin(null);

            }
            if (main == null) main = new JTabsPlugin(app.getFactory("org.tinyrcp.tabs.JTabsFactory"));

            JComponent jcomp = main.getVisualComponent();
            add(jcomp, BorderLayout.CENTER);
            main.setup(app, null);
            main.configure(mainNode);

            setLocation(Integer.parseInt(root.getAttribute("x")), Integer.parseInt(root.getAttribute("y")));
            setSize(Integer.parseInt(root.getAttribute("width")), Integer.parseInt(root.getAttribute("height")));

            //--- Open the frames
            for (int i = 0; i < windowConfs.size(); i++) {
                Element e = windowConfs.get(i);

                //--- Find the content config
                Element c = (Element) e.getElementsByTagName("Content").item(0);
                TinyFactory fac = app.getFactory(c.getAttribute("factory"));
                if (fac != null) {
                    TinyPlugin p = fac.newPlugin(null);
                    p.setup(app, null);
                    p.configure(c);
                    jcomp = p.getVisualComponent();
                    jcomp.putClientProperty("plugin", p);

                    int x = 100;
                    int y = 100;
                    int width = 640;
                    int height = 480;
                    try {
                        x = Integer.parseInt(e.getAttribute("x"));
                        y = Integer.parseInt(e.getAttribute("y"));
                        width = Integer.parseInt(e.getAttribute("width"));
                        height = Integer.parseInt(e.getAttribute("height"));
                        
                    } catch (NumberFormatException ex) {
                        //---
                    }
                    JFrame jframe = new JFrame(e.getAttribute("title"));
                    jframe.getContentPane().add(jcomp);
                    jframe.setSize(width, height);
                    jframe.setLocation(x, y);
                    jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    jframe.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                            JFrame jframe = (JFrame) evt.getWindow();
                            int rep = JOptionPane.showConfirmDialog(jframe, "Do you really want to close this window ?");
                            if (rep == JOptionPane.YES_OPTION) {
                                JComponent jcomp = (JComponent) jframe.getContentPane().getComponent(0);
                                jframe.remove(jcomp);

                                TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
                                p.cleanup();

                                jframe.setVisible(false);
                                jframe.dispose();

                                frames.remove(jframe);
                            }
                        }
                    });
                    jframe.setVisible(true);
                    frames.add(jframe);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }

    }

    protected void close() {
        Object options[] = {"Quit", "Save config and quit", "Cancel"};
        int rep = JOptionPane.showOptionDialog(this, "Quit application", "", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
        if (rep == JOptionPane.NO_OPTION) {
            save(file);

        } else if (rep == JOptionPane.CANCEL_OPTION) {
            return;

        } else if (rep == JOptionPane.CLOSED_OPTION) {
            //---
        }

        //--- Clear  all opened frames
        for (int i = 0; i < frames.size(); i++) {
            JFrame jframe = frames.get(i);
            JComponent jcomp = (JComponent) jframe.getContentPane().getComponent(0);
            jframe.remove(jcomp);

            TinyPlugin p = (TinyPlugin) jcomp.getClientProperty("plugin");
            p.cleanup();

            jframe.setVisible(false);
            jframe.dispose();
        }
        frames.clear();

        jplugins.setVisible(false);
        jplugins.dispose();

        jsettings.setVisible(false);
        jsettings.dispose();

        setVisible(false);
        dispose();

        /*
        //--- Clear all opened frames, this one will use the default events
        //--- but some frame could ask some feed back, so do not use it here
        Window frames[] = Window.getWindows();
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] == this) continue;
            //--- Sent to all frames the closing event
            System.out.println("(I) Send closing event to frame :" + frames[i].getName() + " (" + frames[i].getClass().getName() + ")");
            frames[i].dispatchEvent(new WindowEvent(frames[i], WindowEvent.WINDOW_CLOSING));

        }
         */
        app.destroy();

        System.runFinalization();
        System.gc();

        //--- Wait that the JVM dies
        System.out.println("(I) No System.exit called, should stop automatically");

    }

    /**
     * Main entry point<p>
     *
     * The class loader should be a JarClassLoader, if not, the app was started
     * directly<p>
     *
     * @param args
     */
    public static void main(final String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JarClassLoader loader = null;
                if (getClass().getClassLoader() instanceof JarClassLoader) {
                    loader = (JarClassLoader) getClass().getClassLoader();

                } else {
                    loader = new JarClassLoader();
                    loader.addJar("lib/ext");
                }

                //--- Prepare main resource singleton
                App app = new App(loader, "TinyTCP Example App", null);
                app.initialize();

                //--- Main frame
                JExample jframe = new JExample(app);
                jframe.open();

            }
        });
    }

}
