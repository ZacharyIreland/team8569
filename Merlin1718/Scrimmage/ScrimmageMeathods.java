package org.firstinspires.ftc.teamcode.team.Merlin1718.Scrimmage;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.io.StringWriter;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/* Meathods
 * moveMotorPower(M1p, M2p, M3p, M4p) void - set all drive motor power
 * joyValues () double[] - set joystick values
 * makeFieldOriented (origanalxyz, Orientation Degrees) Double [] - orients drivetrain
 * glyph () void - controls glyph mechinism during teleop
 * sorter(String upOrDown, Sring redOrBlue) Boolean - controls the string
 * moveDirection (String direction, Double power) void - gives us comands that can move the robot in a direction
 * currentEncoder(DcMotor motor, String direction) Boolean Double [] - find ou what the encoder value is
 * turnToGyroHeading(Double target heading, Double current heading) Boolean - turns Gyro to specific heading
 * driveBasedOnEncoders(Double distance, String Direction) Boolean
 * initCamera () void - initializes camera
 * key () String - figuring out wich vuMark you are sensing
 */



class ScrimmageMeathods extends OpMode {

    private ScrimmageHardware robot = new ScrimmageHardware();//The hardware map needs to be the hardware map of the robot we are using

    public void init(){
        robot.init(hardwareMap);
        robot.leftGrasper.setPosition(leftGrasperClosed);
        robot.rightGrasper.setPosition(rightGrasperClosed);
        robot.leftSorter.setPosition(leftSorterUp);
        robot.rightSorter.setPosition(rightSorterUp);
    }
    @Override
    public void init_loop(){}
    @Override
    public void start(){}
    @Override
    public void loop(){}
    @Override
    public void stop(){}
    private double leftGrasperOpen = .25;
    private double rightGrasperOpen = .75;
    private double leftGrasperClosed = .75;
    private double rightGrasperClosed = .27;
    private double leftSorterUp = 0;
    private double leftSorterDown = 1;
    private double rightSorterUp = 1;
    private double rightSorterDown = 0;
    VuforiaLocalizer vuforia;



    public void moveMotorsPower (double Motor1Power, double Motor2Power, double Motor3Power, double Motor4Power){
        //this method rangeclips the motorpower to be from 1 to -1
        robot.Motor1.setPower(Range.clip(Motor1Power, -1, 1));
        robot.Motor2.setPower(Range.clip(Motor2Power,-1,1));
        robot.Motor3.setPower(Range.clip(Motor3Power, -1, 1));
        robot.Motor4.setPower(Range.clip(Motor4Power, -1, 1));
    }
    public double[] joyValues(){
        double[] joyXYZ;
        joyXYZ = new double[3];
        //Set value x to the x axis on the left joystick if the value is above .01
        if(Math.abs(gamepad1.left_stick_x) > .01){
            joyXYZ[0] = gamepad1.left_stick_x;
            //otherwise set vaue to 0
        } else {
            joyXYZ[0] = 0;
        }
        //Set value y to the y axis on the left joystick if the value is above .01
        if(Math.abs(gamepad1.left_stick_y) > .01){
            joyXYZ[1] = -gamepad1.left_stick_y;
            //otherwise set value to 0
        } else {
            joyXYZ[1] = 0;
        }
        //Set value z to the z axis on the left trigger if the value is above .01
        if(gamepad1.left_trigger > .01){
            joyXYZ[2] = -gamepad1.left_trigger;
            //Set value z to the z axis on the right trigger if the value is above .01
        } else if (gamepad1.right_trigger > .01){
            joyXYZ[2] = gamepad1.right_trigger;
            //if the value is not above .01 set the value to 0
        } else {
            joyXYZ[2] = 0;
        }
        return joyXYZ;
    }
            //Make divetrain fieldOriented to the driver
    public double[] makeFieldOriented(double[] originalXYZ, double OrientationDegrees) {

        double[] fieldOrientedXYZ = new double[3];

        double OrientationRadians = OrientationDegrees * Math.PI / 180;
        fieldOrientedXYZ[0] = -originalXYZ[1] * Math.sin(OrientationRadians) + originalXYZ[0] * Math.cos(OrientationRadians);
        fieldOrientedXYZ[1] = originalXYZ[1] * Math.cos(OrientationRadians) + originalXYZ[0] * Math.sin(OrientationRadians);
        fieldOrientedXYZ[2] = originalXYZ[2];

        return fieldOrientedXYZ;
    }
    //If the absalute value of x or the absalute value of y is greater than 0.01 then spin each motor accordingly
    public void drive(double[] givenXYZ) {
        if (Math.abs(givenXYZ[0]) >= 0.01 || Math.abs(givenXYZ[1]) >= 0.01) {

            double Motor1Power = givenXYZ[1] - givenXYZ[0];
            double Motor2Power = givenXYZ[1] + givenXYZ[0];
            double Motor3Power = givenXYZ[1] - givenXYZ[0];
            double Motor4Power = givenXYZ[1] + givenXYZ[0];

            moveMotorsPower(Motor1Power, Motor2Power, Motor3Power, Motor4Power);
            // otherwise move motors accordingly
        } else if (Math.abs(givenXYZ[2]) >= .01) {
            double Motor1Power = -givenXYZ[2] * .5;
            double Motor2Power = givenXYZ[2] * .5;
            double Motor3Power = givenXYZ[2] * .5;
            double Motor4Power = -givenXYZ[2] * .5;

            moveMotorsPower(Motor1Power, Motor2Power, Motor3Power, Motor4Power);

        } else {//If none of these is true turn the power off to the motors to stop the robot
            moveMotorsPower(0, 0, 0, 0);
        }
    }
    public void glyph(){
        //This is the code for our glyph collecting mechanism during TeleOp
        if (gamepad1.x) {//Raise mecanism
            robot.glyph.setPower(.5);
        }
        else if (gamepad1.y) {//Lower mecanism
            robot.glyph.setPower(-.5);
        }
        else {
            robot.glyph.setPower(0);
        }

        if (gamepad1.a) {//close gripper
            robot.leftGrasper.setPosition(leftGrasperClosed); // glyphAuto("close");
            robot.rightGrasper.setPosition(rightGrasperClosed);
        }
        else if (gamepad1.b) {//open gripper
            robot.leftGrasper.setPosition(leftGrasperOpen); // glyphAuto("open");
            robot.rightGrasper.setPosition(rightGrasperOpen);
        }
    }

    //Autonomous
    private double OneRotation = 1120;//Then turn it to actual distance
    private double WheelSize = 4*Math.PI;
    private double startEncoder;//Used in driveBasedOnEncoders to remember the starting encoder value
    private boolean firstTime = true;//Used in driveBasedOnEncoders and launch ball to signify the first time the method has run
    private double DistanceTraveled = 0;//Used in driveBasedOnEncoders to remember how far the robot has gone
    private double redJewelThreshold = 0;
        //This part of code desides wich sorter we are going to use depending as to which side of the field we are on
    public boolean sorter (String upOrDown, String redOrBlue) {
        if(redOrBlue.equals("red")){
            if(upOrDown.equals("up")) robot.rightSorter.setPosition(rightSorterUp);
            else robot.rightSorter.setPosition(rightSorterDown);
        } else {
            if(upOrDown.equals("up")) robot.leftSorter.setPosition(leftSorterUp);
            else robot.leftSorter.setPosition(leftSorterDown);
        }
        return true;
    }
    //use the proper color sensor and have it tell the robot wich color that it senses
    private String jewelColor (String color) {
        ColorSensor colorSensor;
        if(color.equals("red")){
            colorSensor = robot.rightColor;
        } else {
            colorSensor = robot.leftColor;
        }
        if (colorSensor.red() > redJewelThreshold) return "red";

        return "blue";
    }
    //if openOrClose variable = open then open the grasper
    public boolean glyphAuto (String openOrClose){
        if(openOrClose.equals("open")){
            robot.leftGrasper.setPosition(leftGrasperOpen);
            robot.rightGrasper.setPosition(rightGrasperOpen);
        } else{
            robot.leftGrasper.setPosition(leftGrasperClosed);
            robot.rightGrasper.setPosition(rightGrasperClosed);
        }
        return true;
    }
    //This makes it so that if we say a command then it will move accordingly
    private void moveDirection (String direction, double power) {
        switch (direction){
            case "Forward":
                moveMotorsPower(power, power, power, power);
                break;
            case "Back":
                moveMotorsPower(-power, -power, -power, -power);
                break;
            case "Left":
                moveMotorsPower(power, -power, power, -power);
                break;
            case "Right":
                moveMotorsPower(-power, power, -power, power);
                break;
            default:
                telemetry.addData("DIRECTION Broke it", "Yes it did");
                telemetry.update();
                break;


        }
    }
    //This finds out what the encoderis at when the code starts up
    private double currentEncoder (DcMotor motor, String direction) {
        double currentEncoder = 0;
        if(firstTime) {//If it is the first time running the code get the starting value
            startEncoder = motor.getCurrentPosition();
            firstTime = false;
        }
        //If it is not the first time get how far the encoders have gone
        switch (direction) {
            case "Forward":
                currentEncoder = motor.getCurrentPosition() - startEncoder;
                break;
            case "Back":
                currentEncoder = startEncoder - motor.getCurrentPosition();
                break;
            case "Left":
                currentEncoder = motor.getCurrentPosition() - startEncoder;
                break;
            case "Right":
                currentEncoder = motor.getCurrentPosition() - startEncoder;
                break;
        }
        return currentEncoder;
    }
    boolean turnToGyroHeading(double targetHeading, double currentHeading) {//Working and will turn the robot to a gyro heading within 2degrees
        boolean returnValue;//The value the method will return
        double headingDifference = targetHeading - currentHeading;//How far the robot is from its target heading
        double headingScaler = .005;//The scalier that edits how much the speed is affect
        double headingDiffernceScalled = headingDifference * headingScaler;//The scaled value that is used for the motor power
        headingDiffernceScalled = Range.clip(headingDiffernceScalled, -1, 1);//Making sure that the number is within a reasonable motor power

        if(headingDiffernceScalled < .09 && headingDiffernceScalled > 0){//making sure the motor power is not so low that the robot wont move
            headingDiffernceScalled = .09;
        }
        else if(Math.abs(headingDiffernceScalled) < .09 && headingDiffernceScalled < 0){//making sure the motor power is not so low that the robot wont move
            headingDiffernceScalled = -.09;
            telemetry.addData("I got", "Here");
        }
        else{
            telemetry.addData("Go Robot"," Go");//making sure the motor power is not so low that the robot wont move
        }
        telemetry.addData("HDS", headingDiffernceScalled);//Prints the motor powers
        telemetry.addData("CurrentYAW", currentHeading);//Prints the current angle the robot is at
        moveMotorsPower(-headingDiffernceScalled, headingDiffernceScalled, headingDiffernceScalled, -headingDiffernceScalled);//My method to run the motors
        if (1 >= Math.abs(headingDifference)) {//If it is within 2 degrees I am done
            returnValue = true;
        } else {//Otherwise it isn't done
            returnValue = false;
        }
        return returnValue;
    }


        //Drive a certain distance based on encoders
    boolean driveBasedOnEncoders(double distance, String direction){
        boolean returnValue;
        DcMotor motor;
        switch (direction){
            case "Forward":
                motor = robot.Motor1;
                break;
            case "Back":
                motor = robot.Motor1;
                break;
            case "Left":
                motor = robot.Motor1;
                break;
            case "Right":
                motor = robot.Motor2;
                break;
            default:
                motor = robot.Motor1;
        }
        double CurrentEncoder = currentEncoder(motor, direction);
        DistanceTraveled = ((Math.abs(CurrentEncoder) / OneRotation) * WheelSize)*1.125;
        telemetry.addData("",CurrentEncoder);
        if (DistanceTraveled > distance) {//If I have gone the distance I want stop moving
            moveMotorsPower(0,0,0,0);
            returnValue = true;
            firstTime = true;
        }
        else {//Otherwise keep going
            returnValue = false;
            moveDirection(direction, .5);
        }

        telemetry.addData("Distance", DistanceTraveled);
        return returnValue;

    }
        //initializes camera
    public void initCamera() {
                /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View, to save power
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        /*
         * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
         * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
         * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
         * web site at https://developer.vuforia.com/license-manager.
         *
         * Vuforia license keys are always 380 characters long, and look as if they contain mostly
         * random data. As an example, here is a example of a fragment of a valid key:
         *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
         * Once you've obtained a license key, copy the string from the Vuforia web site
         * and paste it in to your code onthe next line, between the double quotes.
         */
        parameters.vuforiaLicenseKey = "AdPKKrD/////AAAAGSIjugGbVECsk0zYoRryiTAFXRdUl/55VWc+O8yUqpFWWbX5fb+/Zve64dxPz4vsZL0Rd4TtPzzSTGDb7NHHnVppmnFp99eLe8jY+q9tvjQ4Iu9kaDaOxTNKRr8kWdWdT7Xa0AksnQ0stzkkHjgxScrOOcA8Poq3+xAEswsM3DW4Di9KeJdQqnX/xa3i5TKzOjO+748hWjwjNcAFoUYjnUbHNp9oYQnYhhiigEHoC0CGAHMTsyYFEKJdwJgcFLsYPqVH/9h/ISSd3saogNwVVpEIVRIu1QL+c7/9h6yKnDdPyV2x1qEZuiXEqTiQJjSt0t3UQ32Q47CO/634+h/VP2HaJHCv9gnJhn7jkRVc6VZA";

        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
    }
    //finds out witch vuMark you are seeing
    public String key (){
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);

        RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

                /* Found an instance of the template. In the actual game, you will probably
                 * loop until this condition occurs, then move on to act accordingly depending
                 * on which VuMark was visible. */
            telemetry.addData("VuMark", "%s visible", vuMark);
            telemetry.update();
            return "" + vuMark;

        }
        else {
            telemetry.addData("VuMark", "not visible");
            telemetry.update();
            return "CantTell";
        }

    }
    private String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }


}
