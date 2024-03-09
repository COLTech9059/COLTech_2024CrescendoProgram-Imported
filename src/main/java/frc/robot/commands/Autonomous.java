package frc.robot.commands;

import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class Autonomous extends SequentialCommandGroup {

    public Autonomous(DriveTrain dT, LimeLight LL){
        //Add more commands to this list to increase the functionality of Autonomous.
        // addCommands(new LimeLightCommand(LL, dT));
    }
}
