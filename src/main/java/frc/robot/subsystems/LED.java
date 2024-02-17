package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.Constants;

public class LED 
{

    private AddressableLED ledBoard = new AddressableLED(Constants.ledPort);
    
    private AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(8*32);

    public LED()
    {

    }

        //#INITIALIZELED
        //This method initializes the LED board
        public void initializeLED() 
        {
        ledBoard.setLength(ledBuffer.getLength());
        ledBoard.setData(ledBuffer);
        }

        //#SETBOARD
        //THis method will the whole LED board to one color
        public void setBoard(String color) 
        {
            if (color.equals("blue")) 
            {
                for (var i = 0; i < ledBuffer.getLength(); i++) 
                {
                    // Sets the specified LED to the RGB values for red
                    ledBuffer.setRGB(i, 0, 0, 255);
                    ledBoard.setData(ledBuffer);
                 }
            }

            if (color.equals("red")) 
            {
                for (var i = 0; i < ledBuffer.getLength(); i++) 
                {
                    // Sets the specified LED to the RGB values for red
                    ledBuffer.setRGB(i, 255, 0, 0);
                    ledBoard.setData(ledBuffer);
                 }
            }

            if (color.equals("green")) 
            {
                for (var i = 0; i < ledBuffer.getLength(); i++) 
                {
                    // Sets the specified LED to the RGB values for red
                    ledBuffer.setRGB(i, 0, 255, 0);
                    ledBoard.setData(ledBuffer);
                 }
            }

            if (color.equals("pink")) 
            {
                for (var i = 0; i < ledBuffer.getLength(); i++) 
                {
                    // Sets the specified LED to the RGB values for red
                    ledBuffer.setRGB(i, 255, 192, 203);
                    ledBoard.setData(ledBuffer);
                 }
            }
        }



        //#SETBOARD
        //This method sets all the LEDs to the RGB values that the user inputs
        public void setBoard(int r, int g, int b) 
        {
            for (var i = 0; i < ledBuffer.getLength(); i++) 
            {
                //Sets the specified LED to the RGB values input to the method
                ledBuffer.setRGB(i, r, g, b);
                ledBoard.setData(ledBuffer);
            }
        }

}
