package de.faz.modules.query.polopoly.mapping;

import de.faz.modules.query.fields.MapToField;
import de.faz.modules.query.fields.Mapping;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/** @author Andreas Kaubisch <a.kaubisch@faz.de> */
public interface PolopolyMapping extends Mapping {
	@MapToField("inputTemplate")
	@Nullable
	String getInputTemplate();

	@MapToField("contentId")
	@Nonnull
	String getContentId();

	@MapToField("publishingDate")
	@Nullable
	Date getPublishingDate();

	@MapToField("commitId")
	@Nullable
	Long getCommitId();

	@MapToField("modifiedDate")
	@Nullable
	Date getModifiedDate();

	@MapToField("offTime")
	@Nullable
	Date getOffTime();

	@MapToField("offTimeDefined")
	@Nullable
	Boolean getOffTimeDefined();

	@MapToField("onTime")
	@Nullable
	Date getOnTime();

	@MapToField("onTimeDefined")
	@Nullable
	Boolean getOnTimeDefined();

	@MapToField("visibleOnline")
	@Nullable
	Boolean getVisibleOnline();

	@MapToField("p.needsIndexing_b")
	@Nullable
	Boolean getNeedsIndexing();

	@MapToField("workflowState")
	@Nullable
	String getWorkflowState();

	@MapToField("lastIndexed")
	@Nullable
	public Date getLastIndexed();
}
