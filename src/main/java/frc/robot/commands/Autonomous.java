package frc.robot.commands;

import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class Autonomous extends SequentialCommandGroup {
    
    public Autonomous(DriveTrain dT, LimeLight LL, Manipulator M, int id)
    {
        if (id == 0) {/*This is meant to do nothing*/};

        if (id == 1)
        {
            addCommands
            (
                new ResetPosition(M),
                new SpeakerScore(M, true, true, 2.2, 3),
                new TurnWithTimer(dT, 0.35, 2),
                new MoveForwardInches(dT, M, 0.35, 48.0, false)
            );
        }
        if (id == 2)
        {
            addCommands
            (
                new ResetPosition(M),
                new SpeakerScore(M, true, true, 2.2, 3),
                new MoveForwardInches(dT, M, 0.35, 18.5, true),
                // new LimeLightCommand(LL, dT),
                new MoveForwardInches(dT, M, -0.35, 17.0, false),
                new SpeakerScore(M, true, false, 0.6, 1.2),
                new MoveForwardInches(dT, M, 0.35, 35.0, false)
            );
        }
        if (id == 3)
        {
            addCommands
            (
                new ResetPosition(M),
                new SpeakerScore(M, true, true, 2.2, 3),
                new TurnWithTimer(dT, -0.35, 1.35),
                new MoveForwardInches(dT, M, 0.35, 48.0, false)
            );
        }
        if (id == 4)
        {
            addCommands
            (
                new ResetPosition(M),
                new SpeakerScore(M, true, true, 2.2, 3)
            );
        }
    }
}
