(ns leaflike.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [hiccup.form :as f]))

(defn search-form
  []
  (f/form-to {:class "form-inline" :role "form"}
             [:get "/bookmarks/search/page/1"]
             [:span
              (f/text-field {:class "form-control" :placeholder "Search"
                             :required ""} "search_query")]))

(defn application
  [title content & {:keys [username error-msg success-msg]}]
  (html5 [:head
          [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
          [:meta {:charset "utf-8"}]
          [:title title]
          (include-css "//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css")
          (include-css "//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css")
          (include-css "/css/styles.css")
          (include-css "/css/select2.min.css")
          (include-js "/js/jquery.min.js")
          (include-js "/js/select2.min.js")
          [:link {:rel "shortcut icon"
                  :href "/favicon.ico?"
                  :type "image/x-icon"}]]
         [:body
           [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
            [:a.navbar-brand {:href "/"} [:img {:src "/logo.png"
                                                :width "150px"
                                                :alt "Leaflike"}]]
            [:button.navbar-toggler {:type "button" :data-toggle "collapse"
                                     :data-target "#navBarSupportCont" :aria-controls "navBarSupportCont"
                                     :aria-expanded "false" :aria-label "Toggle navigation"}
             [:span.navbar-toggler-icon]]
            (when username
              [:div#navBarSupportCont.collapse.navbar-collapse
               [:ul.navbar-nav.mr-auto.mt-2.mt-lg-0
                [:li.nav-item
                 [:a.nav-link {:href "/bookmarks"} "Bookmarks"]]
                [:li.nav-item
                 [:a.nav-link {:href "/bookmarks/readlist"} "Read"]]
                [:li.nav-item
                 [:a.nav-link {:href "/bookmarks/favlist"} "Favorite"]]
                [:li.nav-item
                 [:a.nav-link {:href "/tags"} "Tags"]]]
               (search-form)
               (str "&nbsp;&nbsp;&nbsp;")
               [:div
                [:a {:href "/bookmarks/add" :data-toggle "tooltip" :title "Add bookmark"}
                 [:button {:class "btn btn-large btn-success"}
                  [:i.fa.fa-plus]]]
                (str "&nbsp;&nbsp")
                [:a {:href "/bookmarks/import" :data-toggle "tooltip" :title "Import bookmark"}
                 [:button {:class "btn btn-large btn-primary"}
                  [:i.fa.fa-upload]]]
                (str "&nbsp;&nbsp;&nbsp;")
                [:a {:herf "#" }
                 [:button.btn.btn-outline-secondary
                  [:i.fa.fa-user]
                  (str "&nbsp;&nbsp;" username)]]
                (str "&nbsp;&nbsp;&nbsp;")
                [:a {:href "/logout" :data-toggle "tooltip" :title "Logout"}
                 [:button.btn.btn-outline-danger
                  [:i.fa.fa-sign-out]]]]])]

           [:div.container
            [:div.pb-2.mt-4.mb-2 [:h3 {:class "text-success"} title]]
            [:div#content
             (when error-msg
               [:div {:class "alert alert-danger"} error-msg])
             (when success-msg
               [:div {:class "alert alert-success"} success-msg])
             content]]
          (include-js "//cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js")
          (include-js "//maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js")]))

(defn user-view
  [title username content & {:keys [error-msg success-msg]}]
  (application title content
               :username username
               :error-msg error-msg
               :success-msg success-msg))

(defn index
  []
  [:div {:class "container"}
   [:div {:id "content"}
    [:h1 {:class "text-success"} "Welcome to Leaflike"]
    [:a {:href "/signup"} [:button {:class "btn btn-primary btn-space"} "Signup"]]
    [:a {:href "/login"}  [:button {:class "btn btn-primary btn-space"} "Login"]]]])
