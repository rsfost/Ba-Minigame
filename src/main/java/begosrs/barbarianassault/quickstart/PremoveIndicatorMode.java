package begosrs.barbarianassault.quickstart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PremoveIndicatorMode
{
	DISABLE("Disable"),
	INFO_BOX("Info Box"),
	CHAT("Chat"),
	INFO_BOX_AND_CHAT("Info Box/Chat");

	private final String name;

	public String toString()
	{
		return name;
	}
}
