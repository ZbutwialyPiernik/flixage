package com.zbutwialypiernik.flixage.ui.admin.artist;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.zbutwialypiernik.flixage.entity.Track;

import com.vaadin.flow.component.html.Image;
import com.zbutwialypiernik.flixage.service.file.resource.ImageResource;
import com.zbutwialypiernik.flixage.ui.component.ComponentUtils;

import java.util.Optional;

public class TrackItem extends HorizontalLayout {

    private Track track;

    private final Text nameText;

    private final Image thumbnailImage;

    public TrackItem(Track track, Optional<ImageResource> imageResource) {
        //ComponentUtils.imageFromByteArray(album.get(), album.getId(), "album logo#" + album.getId()),
        nameText = new Text(track.getName());
        thumbnailImage = new Image();

        thumbnailImage.setWidth("128px");
        thumbnailImage.setHeight("128px");

        add(thumbnailImage);
        add(nameText);

        setWidth("300px");

        update(track, imageResource);
    }

    public void update(Track track, Optional<ImageResource> imageResource) {
        nameText.setText(track.getName());
        imageResource.ifPresentOrElse(
                (resource) -> thumbnailImage.setSrc(ComponentUtils.imageFromByteArray(resource)),
                () -> thumbnailImage.setSrc("img/placeholder.jpg"));
    }

}
