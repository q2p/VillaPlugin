package q2p.quickclick.base.queue;

import java.util.function.*;

public interface Queue<Value> {
	boolean isEmpty();

	/** @return Первый элемент в списке или {@code null}, если в списке нет элементов */
	QueueElement<Value> getFirst();
	/** @return Последний элемент в списке или {@code null}, если в списке нет элементов */
	QueueElement<Value> getLast();

	/**
	 * Удаляет первый элемент из списка если таковой присутсвует
	 * @return Первый элемент в списке или {@code null}, если в списке нет элементов
	 */
	QueueElement<Value> removeFirst();
	/**
	 * Удаляет последний элемент из списка если таковой присутсвует
	 * @return Последний элемент в списке или {@code null}, если в списке нет элементов
	 */
	QueueElement<Value> removeLast();

	/** Вставляет элемент в спискок на место первого */
	void insertFirst(QueueElement<Value> element);
	/** Вставляет элемент в спискок на место последнего */
	void insertLast(QueueElement<Value> element);

	void forEach(Consumer<QueueElement<Value>> action);

	/**
	 * Перемещает все эементы из списка в {@code destination}.<br>
	 * <b>Примечание</b>: {@code destination} не должен содержать элементов.
	 */
	void transferIntoEmpty(Queue<Value> destination);

	/** Перемещает все эементы из списка в начало {@code destination} */
	void transferFirst(Queue<Value> destination);

	/** Перемещает все эементы из списка в конец {@code destination} */
	void transferLast(Queue<Value> destination);

	static <Value> Queue<Value> create() {
		return new QueueImpl<>();
	}
}