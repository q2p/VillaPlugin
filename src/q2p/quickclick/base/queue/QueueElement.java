package q2p.quickclick.base.queue;

public interface QueueElement<Value> {
	boolean isQueued();

	void remove();

	Value getValue();

	static <Value> QueueElement<Value> create(final Value value) {
		return new QueueImpl<>(value);
	}
}