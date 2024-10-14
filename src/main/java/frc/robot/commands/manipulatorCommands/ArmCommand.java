package frc.robot.commands.manipulatorCommands;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
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
    private final BooleanSupplier intakePosition;
    private final BooleanSupplier ampPosition;
    private final BooleanSupplier shootPosition;
    // private BooleanSupplier supplyShotB;

    public ArmCommand(Manipulator sentManip, DoubleSupplier armPower, BooleanSupplier shootActive, BooleanSupplier intakeActive, BooleanSupplier canReverseIntake, BooleanSupplier ampActive, BooleanSupplier intakePosition, BooleanSupplier ampPosition, BooleanSupplier shootPosition)
    {
        //Initialize DoubleSuppliers and the Manipulator.
        m_Manipulator = sentManip;
        //Initialize Suppliers/Button Inputs.
        ArmPower = armPower;
        shootEnabled = shootActive;
        this.intakeActive = intakeActive;
        this.canReverseIntake = canReverseIntake;
        this.ampActive = ampActive;
        this.intakePosition = intakePosition;
        this.ampPosition = ampPosition;
        this.shootPosition = shootPosition;
        // this.eStop = eStop;
        // this.supplyShotB = supplyShotB;

        addRequirements(m_Manipulator);

        Shuffleboard.getTab("Manipulator")
        .add("Arm Power", armPower.getAsDouble())
        .withSize(1, 1)
        .withPosition(2, 1);
    }

    @Override
    public void initialize(){
        m_Manipulator.resetEncoders();
    }

    @Override
    public void execute()
    {
        //Move arm based on power
        m_Manipulator.moveArm(ArmPower.getAsDouble());
        m_Manipulator.shootNote(shootEnabled.getAsBoolean(), Constants.shootSpeed);
        m_Manipulator.ampScore(ampActive.getAsBoolean());
        m_Manipulator.intake(2, intakeActive.getAsBoolean());
        //Backup incase the optic sensor is damaged.
        // m_Manipulator.runIntake(canReverseIntake.getAsBoolean(), intakeActive.getAsBoolean());
        m_Manipulator.intakePosition(3, intakePosition.getAsBoolean());
        m_Manipulator.ampPosition(3, ampPosition.getAsBoolean());
        m_Manipulator.variablePosition(3, shootPosition.getAsBoolean(), Constants.shootPosition);
        // m_Manipulator.supplyShot(supplyShotB.getAsBoolean(), 6);
    }


}
