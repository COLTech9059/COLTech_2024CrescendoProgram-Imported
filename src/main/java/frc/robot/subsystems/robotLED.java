package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class robotLED extends SubsystemBase{
     private int height = 24;
     private int width = 32;
     private int port = 0;
    
     private AddressableLED ledPanel = new AddressableLED(port); // Set LED port
     private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(height*width); // Set LED buffer length
    
     private int[][] customBuffer = new int[32][24];
    
     public robotLED(int h, int w, int port) 
     {
        height = h;
        width = w;
        this.port = port;

       ledPanel.setLength(ledBuffer.getLength()); // Set LED strip length
    
       ledPanel.setData(ledBuffer); // Set LED outut to LED buffer
       ledPanel.start(); // Start outputting to LEDs
     }
    
    public void setPlateRGB(int[] color) {
      for(int i = 0; i < ledBuffer.getLength(); i++) {
        ledBuffer.setRGB(i, color[0], color[1], color[2]); // Loop through and set every LED to the color in the buffer
      }
      ledPanel.setData(ledBuffer); // Set the panel to the buffer
    }
    
    public void setPlateHSV(int[] color) {
      for(int i = 0; i < ledBuffer.getLength(); i++) {
        ledBuffer.setRGB(i, color[0], color[1], color[2]); // Loop through and set every LED to the color in the buffer
      }
      ledPanel.setData(ledBuffer); // Set the panel to the buffer
    }
    
    private void drawLineRGB(int x0, int y0, int x1, int y1, int[] color) {
      double dx = Math.abs(x1 - x0);
      int sx = x0 < x1 ? 1 : -1;
      double dy = 0 - Math.abs(y1 - y0);
      int sy = y0 < y1 ? 1 : -1;
      double error = dx +dy;
    
      while (true) {
        //ledBuffer.setRGB(); // EDIT, make it plot at (x0, y0)
        customBuffer[y0][x0] = 1;
        if (x0 == x1 && y0 == y1) break;
        double e2 = 2 * error;
        if (e2 >= dy) {
          if (x0 == x1) break;
          error = error + dy;
          x0 = x0 + sx;
        }
        if (e2 <= dx) {
          if (y0 == y1) break;
          error = error + dx;
          y0 = y0 + sy;
        }
      }
    }

    private Timer delta = new Timer();

    private int r = 0;
    private int g = 0;
    private int b = 255;
    private boolean waitOver = false;
    private boolean boardSet = false;

    public boolean old() 
    {
        delta.start();
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
            if (waitOver == true)
            {
                delta.start();
                if (r > 255) r  = 0;
                if (g > 255) g = 0;
                if (b > 255) b = 0;
                ledBuffer.setRGB(i, r, g, b);
                r += 1;
                g += 2;
                b += 3;
                waitOver = false;
            }
            if (delta.get() > 0.02) 
            {
                waitOver = true;
                delta.reset();
            }
            if (i >= ledBuffer.getLength()) boardSet = true;
        }
        ledPanel.setData(ledBuffer);
        return boardSet;
    }
    
    private boolean finished = true;
    public boolean idleRainbow() 
    {
        if (!finished) return false;
        finished = false;
        for (int i = 0; i < ledBuffer.getLength(); i++)
        {
           delta.start();
            if (r > 255) r  = 0;
            if (g > 255) g = 0;
            if (b > 255) b = 0;
            ledBuffer.setRGB(i, r, g, b);
            r += 1;
            g += 2;
            b += 3;
            waitOver = false;
        }
        ledPanel.setData(ledBuffer);
        finished = true;
        return finished;
    }

    public void idleLines()
    {
        for (int i = 0; i < ledBuffer.getLength(); i += 8)
        {
            // r += 1;
            // g += 2;
            // b += 3;
            for (int j = i; j < i+8; j++)
            {
                // if(j>254) break;
                ledBuffer.setRGB(j, r, g, b);
            }

            // if (r > 255) r = 0;
            // if (g > 255) g = 0;
            // if (b > 255) b = 0;
        }
        ledPanel.setData(ledBuffer);
    }    

    // Never set the values above 128, it causes a seizure
    private Timer boardTime = new Timer();
    private int[] resetColors = new int[]{0, 0, 0};
    private int[] red = new int[]{128, 0, 0};
    private int[] white = new int[]{45, 45, 45};
    @Override
    public void periodic() 
    {
        if (boardTime.get() <= 0) boardTime.start();
        if (boardTime.get() >= 1.5)
        {
            setPlateRGB(white);
            boardTime.reset();
        }
    }
}

