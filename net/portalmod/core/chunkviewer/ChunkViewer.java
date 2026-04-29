package net.portalmod.core.chunkviewer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.minecraft.util.math.ChunkPos;

public class ChunkViewer {
   private static final ChunkViewer INSTANCE = new ChunkViewer();
   private final JFrame window;
   protected final List<ChunkPos> chunks = new ArrayList();

   public ChunkViewer() {
      System.setProperty("java.awt.headless", "false");
      this.window = new JFrame("Chunk Viewer");
      this.window.setContentPane(new ChunkViewPane());
      this.window.getContentPane().setPreferredSize(new Dimension(300, 300));
      this.window.pack();
      this.window.setResizable(false);
   }

   public void setVisible(boolean visible) {
      this.window.setVisible(visible);
   }

   public void refresh() {
      this.window.repaint();
   }

   public List<ChunkPos> getChunkList() {
      return this.chunks;
   }

   public static ChunkViewer getInstance() {
      return INSTANCE;
   }

   private class ChunkViewPane extends JPanel {
      private ChunkViewPane() {
      }

      protected void paintComponent(Graphics g) {
         for(ChunkPos pos : (ChunkPos[])ChunkViewer.this.chunks.toArray(new ChunkPos[0])) {
            g.fillRect(pos.field_77276_a * 1 + 150, pos.field_77275_b * 1 + 150, 1, 1);
         }

      }
   }
}
