package onlinebank;
import java.util.Date;
import java.util.List;

public class HistoryShown extends AbstractEvent {

    private Long id;
    private List<History> listHistory;

	public HistoryShown(){
        super();
    }

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<History> getListHistory() {
		return this.listHistory;
	}

	public void setListHistory(List<History> listHistory) {
		this.listHistory = listHistory;
	}
}
