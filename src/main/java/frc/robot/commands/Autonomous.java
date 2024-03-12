package frc.robot.commands;

import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class Autonomous extends SequentialCommandGroup {

    public Autonomous(DriveTrain dT, LimeLight LL, Manipulator M){
        //Add more commands to this list to increase the functionality of Autonomous.
        addCommands(
            new SpeakerScore(M, true, true, 1.75, 2.5),
            new MoveForwardInches(dT, M, 0.35, 18.5, true),
            // new LimeLightCommand(LL, dT),
            new MoveForwardInches(dT, M, -0.35, 17.0, false),
            new SpeakerScore(M, true, false, 0.6, 1.2),
            new MoveForwardInches(dT, M, 0.35, 35.0, false)
            );
    }
}
