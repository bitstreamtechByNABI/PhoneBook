package contact.phone.nabi.user.model.IpLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpLocation {
	private String city;
    private String region;
    private String country_name;

}
