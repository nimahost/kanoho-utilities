package ec.brooke.kanoho.features.props;

public interface IWrenchGizmo {
    boolean isHovered();

    void setSelected(boolean glowing);

    void startDrag();

    void drag();

    void remove();

    void stopDrag();
}
