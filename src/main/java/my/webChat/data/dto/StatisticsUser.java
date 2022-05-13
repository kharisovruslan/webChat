package my.webChat.data.dto;

import lombok.Data;
import lombok.NonNull;
import my.webChat.data.User;

@Data
public class StatisticsUser implements Comparable {
    @NonNull
    private User user;
    @NonNull
    private long receive;
    @NonNull
    private long write;

    @Override
    public int compareTo(Object o) {
        String n1 = ((StatisticsUser) o).getUser().getUsername();
        String n2 = user.getUsername();
        return n1.compareTo(n2);
    }
}
