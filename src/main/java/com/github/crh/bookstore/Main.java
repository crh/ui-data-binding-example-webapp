package com.github.crh.bookstore;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Label.ContentMode;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * This is a small Vaadin Application to demo its UI Data Binding feature. We
 * take a Book as an example Domain Model. There are two widgets in this
 * application. A Label to shows _only_ the title of the book and a text field
 * for editing the book title.
 */

@SuppressWarnings("serial")
public class Main extends Root {

    private static final String PROPERTY_ID_TITLE = "title";

    /*
     * We put the Book class code inline, for readability purpose. later it will
     * be bound to a Vaadin Item.
     */
    public class Book {
        // ... to a Vaadin Property named 'title'
        String title;

        // ... simplest way to model author, better: Author object
        String author;

        public String getTitle() {
            return title;
        }

        String getAuthor() {
            return author;
        }

        public void setTitle(final String val) {
            title = val;
        }

        void setAuthor(final String val) {
            author = val;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("Book [");
            if (title != null) {
                builder.append("title=").append(title).append(", ");
            }
            if (author != null) {
                builder.append("author=").append(author);
            }
            builder.append("]");
            return builder.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ((author == null) ? 0 : author.hashCode());
            result = prime * result + ((title == null) ? 0 : title.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Book other = (Book) obj;
            if (author == null) {
                if (other.author != null) {
                    return false;
                }
            }
            else if (!author.equals(other.author)) {
                return false;
            }
            if (title == null) {
                if (other.title != null) {
                    return false;
                }
            }
            else if (!title.equals(other.title)) {
                return false;
            }
            return true;
        }
    }

    // We purposely write the code within the init(..) method also for readable
    // reason.
    @Override
    protected void init(@SuppressWarnings("unused") final WrappedRequest request) {
        // UI related builder and configuration
        setStyleName("lion");
        // Application main layout
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        setContent(mainLayout);

        final String fragement = request.getBrowserDetails().getUriFragment();
        if (fragement.equals("livedemo")) {
            showLiveDemo(mainLayout);
        }
        else if (fragement.equals("properties")) {
            showPropertiesDemo(mainLayout);
        }
        else {
            final Label label =
                new Label(
                    "<h1>Please try http://example.org/book/#livedemo, instead of http://example.org/book/#"
                        + request.getBrowserDetails().getUriFragment()
                        + "</h1>", ContentMode.XHTML);
            getContent().addComponent(label);
            return;
        }

    }

    private void showPropertiesDemo(final VerticalLayout mainLayout) {
        // Create an instance of the bean
        final Book book = new Book();
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        // Wrap it in a BeanItem
        final BeanItem<Book> item = new BeanItem<Book>(book);
        // Bind it to a component
        final FieldGroup fieldGroup = new BeanFieldGroup<Book>(Book.class);
        fieldGroup.setItemDataSource(item);

        for (final Object propertyId : fieldGroup.getUnboundPropertyIds()) {
            mainLayout.addComponent(fieldGroup.buildAndBind(propertyId));
        }
    }

    private void showLiveDemo(final VerticalLayout mainLayout) {
        // A read only widget to show the title of a book.
        final HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        final Label forSale = new Label("For Sale ");
        forSale.setReadOnly(true);

        final Label viewer = new Label();
        viewer.setReadOnly(true);

        hl.addComponent(forSale);
        hl.addComponent(viewer);
        mainLayout.addComponent(hl);

        // Edit Widget for changing book's title.
        final TextField editor = new TextField("Book Title");
        editor.setWidth("25%");
        editor.setStyleName("big");
        // editor.setBuffered(true);
        editor.setImmediate(true);
        editor.addShortcutListener(new ShortcutListener(
            "Default", KeyCode.ENTER, null) {

            @Override
            public void handleAction(
                @SuppressWarnings("unused") final Object sender,
                @SuppressWarnings("unused") final Object target) {

                /*
                 * it is a boolean flag to indicate either an update request to
                 * database or web service is successful. // Because we only use
                 * Java In-Memory Object to store our book, the update is
                 * _always_succesful
                 */
                final boolean isUpdateSuccessfull = true;
                if (isUpdateSuccessfull) {
                    /*
                     * we tell the edit widget to trigger change event in Vaadin
                     * Item Data Model. So that other widget gets a notification
                     * to repaint itself, if necessary.
                     */
                    editor.commit();
                }
                else {
                    // tell the user what prevents the update
                }
            }
        });

        mainLayout.addComponent(editor);

        /*
         * We create a book using its default constructor, i.e., the book has
         * empty title and author := ""
         */
        final Book book = new Book();
        book.setTitle("Effective Java");

        // Data Binding related code
        // we then create a Vaadin Item and inject the book model we just
        // created.
        final BeanItem<Book> vaadinItem = new BeanItem<Book>(book);

        /*
         * here where the magic happens. We tell the text field to bind with a
         * Vaadin Property with name "title".
         */
        editor.setPropertyDataSource(vaadinItem
            .getItemProperty(PROPERTY_ID_TITLE));

        // we bind the same Vaadin Property to label.
        // so basically we can bind the same property.
        viewer.setPropertyDataSource(vaadinItem
            .getItemProperty(PROPERTY_ID_TITLE));
    }
}
