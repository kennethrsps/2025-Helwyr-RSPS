package com.rs.utils;

/*
 * @author Dragonkk/Alex
 */
public class MapUtils {

	private static interface StructureEncoder {

		public abstract int encode(int x, int y, int plane);

	}
	
	private static interface StructureDecoder {

		public abstract int[] decode(int id);

	}

	public static enum Structure {

		TILE(null, 1, 1, new StructureEncoder() {
			@Override
			public int encode(int x, int y, int plane) {
				return y | (x << 14) | (plane << 28);
			}
		}, new StructureDecoder() {
			@Override
			public int[] decode(int id) {
				return new int[]{id >> 14 & 16383, id & 16383, id >> 28 & 3};
			}	
		}), CHUNK(TILE, 8, 8, new StructureEncoder() {
			@Override
			public int encode(int x, int y, int plane) {
				return (x << 14) | (y << 3) | (plane << 24);
			}
		}, new StructureDecoder() {
			@Override
			public int[] decode(int id) {
				return new int[]{id >> 14 & 2047, id >> 3 & 2047, id >> 24 & 3};
			}	
		}), REGION(CHUNK, 8, 8, new StructureEncoder() {
			@Override
			public int encode(int x, int y, int plane) {
				return ((x << 8) | y | (plane << 16));
			}
		}, new StructureDecoder() {
			@Override
			public int[] decode(int id) {
				return new int[]{id >> 8 & 255, id & 255, id >> 24 & 3};
			}	
		}), MAP(REGION, 256, 256);

		private Structure child;
		private int width, height;
		private StructureEncoder encoder;
		private StructureDecoder decoder;

		/*
		 * width * height squares.
		 * For instance 4x4:
		 * S S S S
		 * S S S S
		 * S S S S
		 */

		private Structure(Structure child, int width, int height, StructureEncoder encode, StructureDecoder decoder) {
			this.child = child;
			this.width = width;
			this.height = height;
			this.encoder = encode;
			this.decoder = decoder;
		}

		private Structure(Structure child, int width, int height) {
			this(child, width, height, null, null);
		}

		public int getWidth() {
			int x = width;
			Structure nextChild = child;
			while (nextChild != null) {
				x *= nextChild.width;
				nextChild = nextChild.child;
			}
			return x;
		}

		public int getChildWidth() {
			return width;
		}

		public int getHeight() {
			int y = height;
			Structure nextChild = child;
			while (nextChild != null) {
				y *= nextChild.height;
				nextChild = nextChild.child;
			}
			return y;
		}

		public int encode(int x, int y) {
			return encode(x, y, 0);
		}

		public int[] decode(int id) {
			return decoder == null ? null : decoder.decode(id);
		}
		
		public int encode(int x, int y, int plane) {
			return encoder == null ? -1 : encoder.encode(x, y, plane);
		}

		public int getChildHeight() {
			return width;
		}

		@Override
		public String toString() {
			return Utils.formatString(name());
		}
	}

	public static final class Area {

		private Structure structure;
		private int x, y, width, height;

		public Area(Structure structure, int x, int y, int width, int height) {
			this.structure = structure;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getMapX() {
			return x * structure.getWidth();
		}

		public int getMapY() {
			return y * structure.getHeight();
		}

		public int getMapWidth() {
			return width * structure.getWidth();
		}

		public int getMapHeight() {
			return height * structure.getHeight();
		}

		public Structure getStructure() {
			return structure;
		}

		@Override
		public int hashCode() {
			return structure.encode(x, y, 0);
		}

		@Override
		public String toString() {
			return "Structure: " + structure.toString() + ", x: " + x + ", y: " + y + ", width: " + width + ", height: " + height;
		}
	}

	public static Area getArea(int minX, int minY, int maxX, int maxY) {
		return getArea(Structure.TILE, minX, minY, maxX, maxY);
	}

	public static Area getArea(Structure structure, int minX, int minY, int maxX, int maxY) {
		return new Area(structure, minX, minY, maxX - minY, maxY - minY);
	}

	/*
	 * returns converted area
	 */
	public static Area convert(Structure to, Area area) {
		int x = area.getMapX() / to.getWidth();
		int y = area.getMapY() / to.getHeight();
		int width = area.getMapWidth() / to.getWidth();
		int height = area.getMapHeight() / to.getHeight();
		return new Area(to, x, y, width, height);
	}

	/*
	 * converted pos
	 * return converted x and y 
	 */
	public static int[] convert(Structure from, Structure to, int... xy) {
		return new int[]
		{ xy[0] * from.getWidth() / to.getWidth(), xy[1] * from.getHeight() / to.getHeight() };
	}

	public static int encode(Structure structure, int... xyp) {
		return structure.encode(xyp[0], xyp[1], xyp.length == 3 ? xyp[2] : 0);
	}
	
	public static int[] decode(Structure structure, int id) {
		return structure.decode(id);
	}

}