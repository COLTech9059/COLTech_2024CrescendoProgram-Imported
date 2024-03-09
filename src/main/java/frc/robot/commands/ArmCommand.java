package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Manipulator;
import java.util.function.DoubleSupplier;
import java.util.function.BooleanSupplier;

public class ArmCommand extends Command{

    private final Manipulator m_Manipulator;

    //Drive controller related objects
    private final DoubleSupplier ArmPower;
    private final BooleanSupplier shootEnabled;
    private final BooleanSupplier intakeActive;
    private final BooleanSupplier canReverseIntake;
    private final BooleanSupplier ampActive;

    //Operator controller
    private final BooleanSupplier holdManipulator;
    private final BooleanSupplier intakePosition;
    private final BooleanSupplier ampPosition;
    private final BooleanSupplier shootPosition;

    public ArmCommand(Manipulator sentManip, DoubleSupplier armPower, BooleanSupplier shootActive, BooleanSupplier intakeActive, BooleanSupplier canReverseIntake, BooleanSupplier ampActive, BooleanSupplier holdManipulator, BooleanSupplier intakePosition, BooleanSupplier ampPosition, BooleanSupplier shootPosition)
    {
        //Initialize DoubleSuppliers and the Manipulator.
        m_Manipulator = sentManip;
        //For Evan: we can use a singular armPower variable to make things simpler
        //and also be able to control arm speed with the other trigger.
        ArmPower = armPower;
        shootEnabled = shootActive;
        this.intakeActive = intakeActive;
        this.canReverseIntake = canReverseIntake;
        this.ampActive = ampActive;
        this.holdManipulator = holdManipulator;
        this.intakePosition = intakePosition;
        this.ampPosition = ampPosition;
        this.shootPosition = shootPosition;

        addRequirements(m_Manipulator);
    }

    @Override
    public void execute()
    {
        //Move arm based on power
        m_Manipulator.moveArm(ArmPower.getAsDouble());
        m_Manipulator.shootNote(shootEnabled.getAsBoolean());
        m_Manipulator.ampScore(ampActive.getAsBoolean());
        m_Manipulator.intake(2, intakeActive.getAsBoolean());
        m_Manipulator.holdManipulator(holdManipulator.getAsBoolean());
        m_Manipulator.intakePosition(3, intakePosition.getAsBoolean());
        m_Manipulator.ampPosition(3, ampPosition.getAsBoolean());
        m_Manipulator.shootPosition(3, shootPosition.getAsBoolean());

        SmartDashboard.putNumber("ArmPower", ArmPower.getAsDouble());
    }


}
