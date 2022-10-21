package thederpgamer.structurelib.data;

import org.schema.common.FastMath;
import org.schema.common.util.linAlg.Vector3fTools;
import org.schema.game.common.data.SegmentPiece;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class CubeStructure extends Structure {

	public CubeStructure() {
		super(StructureShape.CUBE);
	}

	@Override
	public boolean isValid(SegmentPiece[] segmentPieces, int type) {
		//Put positions of all segment pieces into an array
		Vector3f[] positions = new Vector3f[segmentPieces.length];
		for(int i = 0; i < segmentPieces.length; i ++) positions[i] = new Vector3f(segmentPieces[i].x, segmentPieces[i].y, segmentPieces[i].z);
		//Figure out the dimensions
		float width = 0;
		float height = 0;
		float depth = 0;
		for(int i = 0; i < positions.length; i ++) {
			for(int j = 0; j < positions.length; j ++) {
				if(i == j) continue;
				float distance = Vector3fTools.distance(positions[i].x, positions[i].y, positions[i].z, positions[j].x, positions[j].y, positions[j].z);
				if(distance > width) width = distance;
				if(distance > height) height = distance;
				if(distance > depth) depth = distance;
			}
		}

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
			Vector3f[] testPoints = createPoints(width, height, depth, type, rotations[attempts]);
			for(int i = 0; i < testPoints.length; i ++) {
				boolean found = false;
				for(Vector3f position : positions) {
					if(Vector3fTools.distance(testPoints[i].x, testPoints[i].y, testPoints[i].z, position.x, position.y, position.z) <= 0) {
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
		int width = (int) args[0];
		int height = (int) args[1];
		int depth = (int) args[2];
		int type = (int) args[3];
		Matrix3f rotation = (Matrix3f) args[4];
		switch(type) {
			case HOLLOW:
				return createHollowCube(width, height, depth, rotation);
			case SIDES_ONLY:
				return createWallsAndFloorsOnlyCube(width, height, depth, rotation);
			case FILLED:
				return createFilledCube(width, height, depth, rotation);
			default:
				return null;
		}
	}

	private Vector3f[] createHollowCube(int width, int height, int depth, Matrix3f rotation) {
		//Create a cube that is only the outer edges (frame) of the cube
		Vector3f[] points = new Vector3f[width * height * depth];
		int index = 0;
		for(int x = 0; x < width; x ++) {
			for(int y = 0; y < height; y ++) {
				for(int z = 0; z < depth; z ++) {
					if(x == 0 || x == width - 1 || y == 0 || y == height - 1 || z == 0 || z == depth - 1) {
						points[index] = new Vector3f(x, y, z);
						index ++;
					}
				}
			}
		}

		//Trim null entries
		Vector3f[] trimmedPoints = new Vector3f[index];
		System.arraycopy(points, 0, trimmedPoints, 0, trimmedPoints.length);

		//Rotate the points
		for(Vector3f point : trimmedPoints) rotation.transform(point);
		return trimmedPoints;
	}

	private Vector3f[] createWallsAndFloorsOnlyCube(int width, int height, int depth, Matrix3f rotation) {
		//Create a hollow cube
		Vector3f[] points = new Vector3f[width * height * depth];
		int index = 0;
		for(int x = 0; x < width; x ++) {
			for(int y = 0; y < height; y ++) {
				for(int z = 0; z < depth; z ++) {
					if(x == 0 || x == width - 1 || z == 0 || z == depth - 1) {
						points[index] = new Vector3f(x, y, z);
						index ++;
					}
				}
			}
		}

		//Trim null entries
		Vector3f[] trimmedPoints = new Vector3f[index];
		System.arraycopy(points, 0, trimmedPoints, 0, trimmedPoints.length);

		//Rotate the points
		for(Vector3f point : trimmedPoints) rotation.transform(point);
		return trimmedPoints;
	}

	private Vector3f[] createFilledCube(int width, int height, int depth, Matrix3f rotation) {
		//Create a filled cube
		Vector3f[] points = new Vector3f[width * height * depth];
		int index = 0;
		for(int x = 0; x < width; x ++) {
			for(int y = 0; y < height; y ++) {
				for(int z = 0; z < depth; z ++) {
					points[index] = new Vector3f(x, y, z);
					index ++;
				}
			}
		}

		//Trim null entries
		Vector3f[] trimmedPoints = new Vector3f[index];
		System.arraycopy(points, 0, trimmedPoints, 0, trimmedPoints.length);

		//Rotate the points
		for(Vector3f point : trimmedPoints) rotation.transform(point);
		return trimmedPoints;
	}
}
