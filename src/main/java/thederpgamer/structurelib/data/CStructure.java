package thederpgamer.structurelib.data;

import org.schema.common.FastMath;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.data.SegmentPiece;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * Similar to thederpgamer.structurelib.data.Circle, but more of a C or U shape. Also is more lenient with the roundness of the shape, kind of like a rounded rectangle, to allow for shipyard-like structures.
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class CStructure extends Structure {

	public static int MIN_POINTS = 4; //4 points minimum for a C shape

	public CStructure() {
		super(StructureShape.C_SHAPE);
	}

	@Override
	public boolean isValid(SegmentPiece[] segmentPieces) {
		//Put positions of all segment pieces into an array
		Vector3f[] positions = new Vector3f[segmentPieces.length];
		for(int i = 0; i < segmentPieces.length; i ++) positions[i] = new Vector3f(segmentPieces[i].x, segmentPieces[i].y, segmentPieces[i].z);
		//Figure out the radius
		float radius = 0;
		for(int i = 0; i < positions.length; i ++) {
			for(int j = 0; j < positions.length; j ++) {
				if(i == j) continue;
				float distance = Vector3fTools.distance(positions[i].x, positions[i].y, positions[i].z, positions[j].x, positions[j].y, positions[j].z);
				if(distance > radius) radius = distance;
			}
		}
		radius /= 2;

		//Attempt to match the test points to the actual points. If they don't match, rotate the template points and try again. If all rotations have been tried, the structure is invalid.
		Matrix3f[] rotations = new Matrix3f[3];
		rotations[0] = new Matrix3f();
		rotations[0].setIdentity();
		rotations[1] = new Matrix3f();
		rotations[1].setIdentity();
		rotations[1].rotY(FastMath.PI);
		rotations[2] = new Matrix3f();
		rotations[2].setIdentity();
		rotations[2].rotZ(FastMath.PI);

		//Calculate a maximum points value based on the radius
		int maxPoints = (int) (radius * 2);
		int points = MIN_POINTS;

		//Create the test points, and compare them to the actual points for each rotation. If all rotations fail, try increasing the number of points.
		//If the number of points is already at the maximum, the structure is invalid.
		while(points <= maxPoints) {
			int attempts = 0;
			for(Matrix3f rotation : rotations) {
				Vector3f[] testPoints = createPoints(radius, points, rotation);
				boolean valid = true;
				for(Vector3f testPoint : testPoints) {
					boolean found = false;
					for(Vector3f position : positions) {
						if(Vector3fTools.distance(testPoint.x, testPoint.y, testPoint.z, position.x, position.y, position.z) < 0.5f) {
							found = true;
							break;
						}
					}
					if(!found) {
						valid = false;
						break;
					}
				}
				if(valid) return true;
				attempts ++;
			}
			if(attempts == rotations.length) points ++;
		}
		return false;
	}

	@Override
	public Vector3f[] createPoints(Object... args) {
		float radius = (float) args[0];
		float roundness = (float) args[1];
		Matrix3f rotation = (Matrix3f) args[2];
		//Calculate amount of points to use based on the radius and roundness
		int pointcount = (int) (radius * roundness);
		if(pointcount < MIN_POINTS) pointcount = MIN_POINTS;
		//Create a C Shape
		Vector3f[] points = new Vector3f[pointcount];
		for(int i = 0; i < pointcount; i ++) {
			float angle = (float) (i * (Math.PI * 2) / pointcount);
			float x = (float) (radius * Math.cos(angle));
			float y = (float) (radius * Math.sin(angle));
			points[i] = new Vector3f(x, y, 0);
			rotation.transform(points[i]);
		}
		return points;
	}
}
