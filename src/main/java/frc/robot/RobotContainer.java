package frc.robot;

import frc.robot.subsystems.*;
import frc.robot.commands.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.XboxController;
// import java.util.function.DoubleSupplier;

public class RobotContainer {
    //Subsystem declarations.
    private final DriveTrain m_DriveTrain = new DriveTrain();
    private final LimeLight m_LimeLight = new LimeLight();
    private final Manipulator m_Manipulator = new Manipulator();
    //Setup Controller.
    // private final XboxController xbContMovement = new XboxController(0);
    // private final XboxController xbContArm = new XboxController(1);
    //Autonomous Command.
    private final Command m_autonomousCommand = new Autonomous(m_DriveTrain, m_LimeLight);

    public RobotContainer()
    {
        m_DriveTrain.setDefaultCommand
        (
            new DriveCommand
            (
                m_DriveTrain, 
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
                () -> IO.dController.getRightBumper(), 
                () -> IO.dController.getLeftBumper(),
                () -> IO.dController.getAButton(),
                () -> IO.dController.getBButton(),
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
    public Command getAutoCommand()
    {
        return m_autonomousCommand;
    }
}
