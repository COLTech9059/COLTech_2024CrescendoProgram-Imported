/* This file defines the IO class, which provides a static instance of an XboxController object to be used throughout the robot code. */

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class IO {

    // Define new XboxController object
    public static XboxController dController = new XboxController(0);
    public static XboxController oController = new XboxController(1);
    // public static CommandXboxController commandController = new CommandXboxController(1);
}
