package q2p.quickclick.base.queue;

import java.util.function.*;

final class QueueImpl<Value> implements QueueElement<Value>, Queue<Value> {
	private final Value value;
	public Value getValue() {
		// Not a pointer
		assert next != this && prev != this;

		return value;
	}

	private QueueImpl<Value> prev;
	private QueueImpl<Value> next;

	QueueImpl(final Value value) {
		this.value = value;
		next = null;
		prev = null;
	}
	QueueImpl() {
		value = null;
		next = this;
		prev = this;
	}

	public boolean isQueued() {
		assert next == null && prev == null || next != null && prev != null;

		return next != null;
	}

	public void remove() {
		assert isQueued();
		// Not a pointer
		assert next != this && prev != this;

		next.prev = prev;
		prev.next = next;

		next = null;
		prev = null;
	}

	private boolean possiblyAPointer() {
		return (next != null && prev != null) && (next != this && prev != this || next == this && prev == this);
	}

	public boolean isEmpty() {
		assert possiblyAPointer();

		return next == this;
	}

	public QueueElement<Value> getFirst() {
		assert possiblyAPointer();

		return next == this ? null : next;
	}
	public QueueElement<Value> getLast() {
		assert possiblyAPointer();

		return prev == this ? null : prev;
	}

	public QueueElement<Value> removeFirst() {
		assert possiblyAPointer();

		if(next == this)
			return null;

		final QueueImpl<Value> element = next;

		next = next.next;
		next.prev = this;

		element.next = null;
		element.prev = null;

		return element;
	}
	public QueueElement<Value> removeLast() {
		assert possiblyAPointer();

		if(prev == this)
			return null;

		final QueueImpl<Value> element = prev;

		prev = prev.prev;
		prev.next = this;

		element.next = null;
		element.prev = null;

		return element;
	}

	public void insertFirst(final QueueElement<Value> element) {
		assert possiblyAPointer();

		assert element instanceof QueueImpl;

		final QueueImpl<Value> el = (QueueImpl<Value>) element;

		assert !el.isQueued();

		el.next = next;
		el.prev = this;

		next.prev = el;
		next = el;
	}
	public void insertLast(final QueueElement<Value> element) {
		assert possiblyAPointer();

		assert element instanceof QueueImpl;

		final QueueImpl<Value> el = (QueueImpl<Value>) element;

		assert !el.isQueued();

		el.prev = prev;
		el.next = this;

		prev.next = el;
		prev = el;
	}

	public void forEach(final Consumer<QueueElement<Value>> action) {
		assert possiblyAPointer();

		QueueImpl<Value> p = this;
		QueueImpl<Value> n = next;
		while(n != this) {
			action.accept(n);
			if(p.next == n)
				p = n;

			n = p.next;
		}
	}

	public void transferIntoEmpty(final Queue<Value> destination) {
		assert destination != null && destination instanceof QueueImpl;

		final QueueImpl<Value> dest = (QueueImpl<Value>) destination;

		assert possiblyAPointer();

		assert dest.possiblyAPointer();

		assert dest.isEmpty();

		if(isEmpty())
			return;

		next.prev = dest;
		prev.next = dest;

		dest.next = next;
		dest.prev = prev;

		next = this;
		prev = this;
	}

	public void transferFirst(final Queue<Value> destination) {
		throw new AssertionError("Не правильная имплементация");

		/*assert destination != null && destination instanceof QueueImpl;

		final QueueImpl<Value> dest = (QueueImpl<Value>) destination;

		assert possiblyAPointer();

		assert dest.possiblyAPointer();

		// TODO: destination может быть пустым
		assert dest.isEmpty();

		if(isEmpty())
			return;

		prev.next = dest.next;
		dest.next.prev = prev;

		next.prev = dest;
		dest.next = next;

		next = this;
		prev = this;*/
	}

	public void transferLast(final Queue<Value> destination) {
		throw new AssertionError("Не правильная имплементация");

		/* assert destination != null && destination instanceof QueueImpl;

		final QueueImpl<Value> dest = (QueueImpl<Value>) destination;

		assert possiblyAPointer();

		assert dest.possiblyAPointer();

		// TODO: destination может быть пустым
		assert dest.isEmpty();

		if(isEmpty())
			return;

		next.prev = dest.prev;
		dest.prev.next = next;

		prev.next = dest;
		dest.prev = prev;

		next = this;
		prev = this; */
	}
}