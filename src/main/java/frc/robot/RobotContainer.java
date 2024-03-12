package frc.robot;

import frc.robot.subsystems.*;
import frc.robot.commands.*;

public class RobotContainer {
    //Subsystem declarations.
    private final DriveTrain drivetrain;
    private final LimeLight limelight;
    private final Manipulator m_Manipulator;
    //Setup Controller.
    // private final XboxController xbContMovement = new XboxController(0);
    // private final XboxController xbContArm = new XboxController(1);
    //Autonomous Command.
    // private final Command m_autonomousCommand = new Autonomous(m_DriveTrain, m_LimeLight);

    public RobotContainer(DriveTrain sentDrive, LimeLight sentLime, Manipulator sentManip)
    {
        drivetrain = sentDrive;
        limelight = sentLime;
        m_Manipulator = sentManip;

        drivetrain.setDefaultCommand
        (
            new DriveCommand
            (
                drivetrain, 
                () -> IO.dController.getLeftY(), 
                () -> IO.dController.getRightX()
            )
        );

        m_Manipulator.setDefaultCommand
        (
            new ArmCommand
            (
                m_Manipulator, 
                () -> (IO.dController.getRightTriggerAxis() - IO.dController.getLeftTriggerAxis()), 
                () -> IO.oController.getRightBumper(), 
                () -> IO.dController.getLeftBumper(),
                () -> IO.dController.getAButton(),
                () -> IO.oController.getLeftBumper(),
                () -> IO.oController.getBButton(),
                () -> IO.oController.getAButton(),
                () -> IO.oController.getYButton(),
                () -> IO.oController.getXButton()
             )
        );
        //Future possible commands here.

        configureButtons();
    }

    private void configureButtons()
    {
        
    }

    //For getting the autonomous command.
    // public Command getAutoCommand()
    // {
    //     return m_autonomousCommand;
    // }
}
