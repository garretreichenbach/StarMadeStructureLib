package thederpgamer.structurelib.data;

import org.schema.common.FastMath;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.data.SegmentPiece;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * Standard circle structure.
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class CircleStructure extends Structure {

	public static final int POINTS = 8;
	public static final float TOLERANCE = 0.5f;

	public CircleStructure() {
		super(StructureShape.CIRCLE);
	}

	@Override
	public boolean isValid(SegmentPiece[] segmentPieces, int type) {
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
		int attempts = 0;
		Matrix3f[] rotations = new Matrix3f[3];
		rotations[0] = new Matrix3f();
		rotations[0].setIdentity();
		rotations[1] = new Matrix3f();
		rotations[1].setIdentity();
		rotations[1].rotY(FastMath.PI);
		rotations[2] = new Matrix3f();
		rotations[2].setIdentity();
		rotations[2].rotZ(FastMath.PI);
		while(attempts < 3) {
			Vector3f[] testPoints = createPoints(radius, rotations[attempts]);
			for(int i = 0; i < testPoints.length; i ++) {
				boolean found = false;
				for(Vector3f position : positions) {
					if(Vector3fTools.distance(testPoints[i].x, testPoints[i].y, testPoints[i].z, position.x, position.y, position.z) <= TOLERANCE) {
						found = true;
						break;
					}
				}
				if(!found) break;
				if(i == testPoints.length - 1) return true;
			}
			attempts ++;
		}
		return false;
	}

	@Override
	public Vector3f[] createPoints(Object... args) {
		Vector3f[] points = new Vector3f[POINTS];
		Matrix3f rotation = (Matrix3f) args[1];
		float radius = (float) args[0];
		Vector3f center = new Vector3f();
		int d = (5 - FastMath.round(radius) * 4) / 4;
		int x = 0;
		int y = FastMath.round(radius);

		do {
			points[0] = new Vector3f(center.x + x, center.y + y, center.z);
			points[1] = new Vector3f(center.x + x, center.y - y, center.z);
			points[2] = new Vector3f(center.x - x, center.y + y, center.z);
			points[3] = new Vector3f(center.x - x, center.y - y, center.z);
			points[4] = new Vector3f(center.x + y, center.y + x, center.z);
			points[5] = new Vector3f(center.x + y, center.y - x, center.z);
			points[6] = new Vector3f(center.x - y, center.y + x, center.z);
			points[7] = new Vector3f(center.x - y, center.y - x, center.z);
			rotation.transform(points[0]);
			rotation.transform(points[1]);
			rotation.transform(points[2]);
			rotation.transform(points[3]);
			rotation.transform(points[4]);
			rotation.transform(points[5]);
			rotation.transform(points[6]);
			rotation.transform(points[7]);
			if(d < 0) d += 2 * x + 1;
			else {
				d += 2 * (x - y) + 1;
				y --;
			}
			x ++;
		} while (x <= y);
		return points;
	}
}
