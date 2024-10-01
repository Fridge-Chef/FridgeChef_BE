package Fridge_chef.team.shop;


import java.util.List;


public class NaverSearchResponse {
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<Item> items;

    public NaverSearchResponse() {
    }

    public NaverSearchResponse(String lastBuildDate, int total, int start, int display, List<Item> items) {
        this.lastBuildDate = lastBuildDate;
        this.total = total;
        this.start = start;
        this.display = display;
        this.items = items;
    }


    public static class Item {
        private String title;
        private String link;
        private String image;
        private String lprice;
        private String hprice;
        private String mallName;
        private String productId;
        private String productType;
        private String brand;
        private String maker;
        private String category1;
        private String category2;
        private String category3;
        private String category4;

        public Item() {}

        public Item(String title, String link, String image, String lprice, String hprice, String mallName, String productId, String productType, String brand, String maker, String category1, String category2, String category3, String category4) {
            this.title = title;
            this.link = link;
            this.image = image;
            this.lprice = lprice;
            this.hprice = hprice;
            this.mallName = mallName;
            this.productId = productId;
            this.productType = productType;
            this.brand = brand;
            this.maker = maker;
            this.category1 = category1;
            this.category2 = category2;
            this.category3 = category3;
            this.category4 = category4;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public int getTotal() {
        return total;
    }

    public int getStart() {
        return start;
    }

    public int getDisplay() {
        return display;
    }

    public List<Item> getItems() {
        return items;
    }
    public String firstItemLink(){
        if(items.isEmpty()){
            return "";
        }
        return items.get(0).link;
    }
}
