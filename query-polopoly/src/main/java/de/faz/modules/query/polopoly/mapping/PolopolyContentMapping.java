package de.faz.modules.query.polopoly.mapping;

import com.polopoly.cm.policy.Policy;
import de.faz.modules.query.MappingFor;
import de.faz.modules.query.fields.MapToField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author Andreas Kaubisch <a.kaubisch@faz.de>
 *
 * @see com.polopoly.search.solr.schema.IndexFields for getting all standard polopoly fields.
 */
@MappingFor(Policy.class)
public class PolopolyContentMapping implements PolopolyMapping {

	@Override
	@MapToField("inputTemplate")
	@Nullable
	public String getInputTemplate() {
		return null;
	}

	@Override
	@MapToField("contentId")
	@Nonnull
	public String getContentId() {
		return "";
	}

	@Override
	@MapToField("publishingDate")
	@Nullable
	public Date getPublishingDate() {
		return null;
	}

	@Override
	@MapToField("commitId")
	@Nullable
	public Long getCommitId() {
		return null;
	}

	@Override
	@MapToField("modifiedDate")
	@Nullable
	public Date getModifiedDate() {
		return null;
	}

	@Override
	@MapToField("offTime")
	@Nullable
	public Date getOffTime() {
		return null;
	}

	@Override
	@MapToField("offTimeDefined")
	@Nullable
	public Boolean getOffTimeDefined() {
		return null;
	}

	@Override
	@MapToField("onTime")
	@Nullable
	public Date getOnTime() {
		return null;
	}

	@Override
	@MapToField("onTimeDefined")
	@Nullable
	public Boolean getOnTimeDefined() {
		return null;
	}

	@Override
	@MapToField("visibleOnline")
	@Nullable
	public Boolean getVisibleOnline() {
		return null;
	}

	@Override
	@MapToField("p.needsIndexing_b")
	@Nullable
	public Boolean getNeedsIndexing() {
		return null;
	}

	@Override
	@MapToField("workflowState")
	@Nullable
	public String getWorkflowState() {
		return null;
	}

	@Override
	@MapToField("lastIndexed")
	@Nullable
	public Date getLastIndexed() {
		return null;
	}
}
