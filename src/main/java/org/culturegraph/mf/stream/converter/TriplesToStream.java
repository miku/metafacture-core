/**
 *
 */
package org.culturegraph.mf.stream.converter;

import org.culturegraph.mf.formeta.parser.FormetaParser;
import org.culturegraph.mf.formeta.parser.PartialRecordEmitter;
import org.culturegraph.mf.framework.helpers.DefaultObjectPipe;
import org.culturegraph.mf.framework.StreamReceiver;
import org.culturegraph.mf.framework.annotations.Description;
import org.culturegraph.mf.framework.FluxCommand;
import org.culturegraph.mf.framework.annotations.In;
import org.culturegraph.mf.framework.annotations.Out;
import org.culturegraph.mf.framework.objects.Triple;
import org.culturegraph.mf.framework.objects.Triple.ObjectType;

/**
 * Converts triples into a stream.
 *
 * @author schaeferd
 *
 */
@Description("Converts a triple into a record stream")
@In(Triple.class)
@Out(StreamReceiver.class)
@FluxCommand("triples-to-stream")
public final class TriplesToStream extends
		DefaultObjectPipe<Triple, StreamReceiver> {

	private final FormetaParser parser = new FormetaParser();
	private final PartialRecordEmitter emitter = new PartialRecordEmitter();

	public TriplesToStream() {
		parser.setEmitter(emitter);
	}

	@Override
	public void process(final Triple triple) {
		getReceiver().startRecord(triple.getSubject());
		if(triple.getObjectType() == ObjectType.STRING){
			getReceiver().literal(triple.getPredicate(), triple.getObject());
		}else if (triple.getObjectType() == ObjectType.ENTITY){
			emitter.setDefaultName(triple.getPredicate());
			parser.parse(triple.getObject());
		}else{
			throw new UnsupportedOperationException(triple.getObjectType() + " can not yet be decoded");
		}
		getReceiver().endRecord();
	}

	@Override
	protected void onSetReceiver() {
		emitter.setReceiver(getReceiver());
	}

}
