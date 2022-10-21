package thederpgamer.structurelib.data;

import org.schema.game.common.data.SegmentPiece;

import javax.vecmath.Vector3f;

/**
 * [Description]
 *
 * @author TheDerpGamer (MrGoose#0027)
 */
public abstract class Structure {

	public enum StructureShape {
		CUBE(CubeStructure.class),
		CYLINDER(CylinderStructure.class),
		SQUARE(SquareStructure.class),
		TRIANGLE(TriangleStructure.class),
		CIRCLE(CircleStructure.class),
		C_SHAPE(CStructure.class);

		public final Class<? extends Structure> structureClass;

		StructureShape(Class<? extends Structure> structureClass) {
			this.structureClass = structureClass;
		}

		public static StructureShape getShape(String shape) {
			for(StructureShape structureShape : StructureShape.values()) {
				if(structureShape.name().equalsIgnoreCase(shape)) return structureShape;
			}
			return null;
		}

		public static String[] getShapeNames() {
			String[] shapeNames = new String[StructureShape.values().length];
			for(int i = 0; i < StructureShape.values().length; i ++) {
				shapeNames[i] = StructureShape.values()[i].name();
			}
			return shapeNames;
		}
	}

	public static final int HOLLOW = 0;
	public static final int SIDES_ONLY = 1;
	public static final int FILLED = 2;

	protected final StructureShape structureShape;

	public Structure(StructureShape structureShape) {
		this.structureShape = structureShape;
	}

	public abstract boolean isValid(SegmentPiece[] segmentPieces, int type);

	public abstract Vector3f[] createPoints(Object... args);
}