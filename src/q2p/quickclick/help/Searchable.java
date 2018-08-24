package q2p.quickclick.help;

public interface Searchable {
	/**
	 * @return Индекс элемента.
	 */
	int id();
	
	/**
	 * Ищет объект в отсортированном массиве основываясь на индексе.
	 * @return Позицию объекта с заданным id или -1 если такой объект не присутсвует в массиве.
	 */
	static <T extends Searchable> int posSearch(final T[] array, final int id) {
		assert array != null;
		
		int low = 0;
		int high = array.length - 1;
		
		while(high >= low) {
			final int middle = (low + high) / 2;
			final int lid = array[middle].id();
			
			if(lid == id)
				return middle;
			
			if(lid < id)
				low = middle + 1;
			else if(lid > id)
				high = middle - 1;
		}
		
		return -1;
	}
	
	/**
	 * Ищет объект в отсортированном массиве основываясь на индексе.
	 * @return Объект с заданным id или null если такой объект не присутсвует в массиве.
	 */
	static <T extends Searchable> T instanceSearch(final T[] array, final int id) {
		assert array != null;
		
		int low = 0;
		int high = array.length - 1;
		
		while(high >= low) {
			final int middle = (low + high) / 2;
			final int lid = array[middle].id();
			
			if(lid == id)
				return array[middle];
			
			if(lid < id)
				low = middle + 1;
			else if(lid > id)
				high = middle - 1;
		}
		
		return null;
	}
}