package gov.usgs.volcanoes.winston.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

public class WinstonToolsAboutDialog extends JDialog {

  private static final long serialVersionUID = 1L;
  private final JButton dismiss;

  public WinstonToolsAboutDialog() {
    super();

    dismiss = new JButton("Close");
    dismiss.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        dispose();
      }
    });

    this.add(dismiss, BorderLayout.SOUTH);
    pack();

  }
}
