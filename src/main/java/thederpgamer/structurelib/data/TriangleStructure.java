package thederpgamer.structurelib.data;

import org.schema.game.common.data.SegmentPiece;

import javax.vecmath.Vector3f;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public class TriangleStructure extends Structure {

	public TriangleStructure() {
		super(StructureShape.TRIANGLE);
	}

	@Override
	public boolean isValid(SegmentPiece[] segmentPiece) {
		return false;
	}

	@Override
	public Vector3f[] createPoints(Object... args) {
		return new Vector3f[0];
	}
}
