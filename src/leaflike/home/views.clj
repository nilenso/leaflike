(ns leaflike.home.views)

(defn statistic-card
  [name subtitle stat-prefix stats]
  [:div.card { :style "width: 18 rem;"}
   [:div.card-body {:style "background : rgba(0,0,0,.05);"}
    [:h4.card-title name]
    [:h6.card-subtitle.mb-1.text-muted subtitle]]
   [:div.card-body
    { :style "border-top: 1px solid rgba(0,0,0,.125); padding-bottom: 0rem;"}
    [:p [:b (str stat-prefix " bookmarked: ")] (str (:bookmarked stats))]
    [:p [:b (str stat-prefix " read: ")] (str (:read stats))]
    [:p [:b (str stat-prefix " favorited: ")] (str (:favorite stats))]]])

(defn bookmarks-card
  [name subtitle bookmarks content links?]
  [:div.card
   [:div.card-body {:style "background : rgba(0,0,0,.05);"}
    [:h4.card-title name]
    [:h6.card-subtitle.mb-1.text-muted subtitle]]
   [:ul.list-group.list-group-flush
    (for [bookmark bookmarks]
      [:li.list-group-item
       [:div.row
        [:div.col
         [:a {:href "#"} (:title bookmark)]]
        [:div.col-3
         [:div.well.pull-right
          [:i {:style "font-size:smaller;"} (:subtitle bookmark)]]]]])]
   [:div.card-body
    [:a.card-link.well.pull-right {:href content } "See all "
     [:i.fa.fa-chevron-right]]]])

(defn home-view
  [request]
  [:div {:id "content"}
   [:div.container
    [:div.row
     [:div.col-sm.mb-2.mt-2
      (statistic-card "Last week"
                      "Statistics for last week"
                      "Articles"
                      {:bookmarked 12
                       :read 7
                       :favorite 3})]
     [:div.col-sm.mb-2.mt-2
      (statistic-card "Last month"
                      "Statistics for last month"
                      "Articles"
                      {:bookmarked 30
                       :read 11
                       :favorite 5})]
     [:div.col-sm.mb-2.mt-2
      (statistic-card "Overall"
                      "Overall bookmark statistics"
                      "Total"
                      {:bookmarked 4100
                       :read 123
                       :favorite 450})]]
    [:div.row.mt-4
     [:div.col.mb-2.mt-2
      (bookmarks-card
        "Unread bookmarks"
        "Articles that pending since long time"
        [{:title "Redis Manifesto"
          :subtitle "Pending since 12 Mar 2018"}
         {:title "Never Make Counter-Offers"
          :subtitle "Pending since 11 Dec 2018"}
         {:title "Why, oh WHY, do those #?@! nutheads use vi?"
          :subtitle "Pending since 24 Dec 2018"}
         {:title "Public speaking for normal people"
          :subtitle "Pending since 22 Jan 2019"}
         {:title "Fork and Join: Java Can Excel at Painless Parallel Programming Too!"
          :subtitle "Pending since 14 Feb 2019" }
         {:title "First Impressions Nokia Lumia 800 - Mobile Phone | ThinkDigit Features"
          :subtitle "Pending since 15 Feb 2019"}]
        (str "/bookmarks")
        true)]]]])