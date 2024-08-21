package frc.robot.commands;

import frc.robot.commands.driveCommands.MoveForwardInches;
import frc.robot.commands.driveCommands.TurnWithTimer;
import frc.robot.commands.manipulatorCommands.EncoderSpeakerScore;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class Autonomous extends SequentialCommandGroup 
{
    
    public Autonomous(DriveTrain dT, LimeLight LL, Manipulator M, long id)
    {
        if (id == 0) {/*This is meant to do nothing*/};

        if (id == 1)
        {
            addCommands
            (
                new EncoderSpeakerScore(M),
                new TurnWithTimer(dT, 0.35, 2),
                new MoveForwardInches(dT, M, 0.35, 48.0, false)
            );
        }
        if (id == 2)
        {
            addCommands
            (
                new EncoderSpeakerScore(M),
                new MoveForwardInches(dT, M, 0.35, 18.5, true),
                new MoveForwardInches(dT, M, -0.35, 17.0, false),
                new EncoderSpeakerScore(M),
                new MoveForwardInches(dT, M, 0.35, 35.0, false)
            );
        }
        if (id == 3)
        {
            addCommands
            (
                new EncoderSpeakerScore(M),
                new TurnWithTimer(dT, -0.35, 1.35),
                new MoveForwardInches(dT, M, 0.35, 48.0, false)
            );
        }
        if (id == 4)
        {
            addCommands
            (
                new EncoderSpeakerScore(M)
            );
        }
        if (id == 5)
        {
            addCommands
            (
            new MoveForwardInches(dT, M, 0.35, 70, true)
            );
        }
    }
}
