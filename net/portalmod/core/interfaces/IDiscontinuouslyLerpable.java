package net.portalmod.core.interfaces;

import java.util.Deque;
import net.portalmod.common.sorted.portal.DiscontinuousLerpPos;

public interface IDiscontinuouslyLerpable {
   Deque<DiscontinuousLerpPos> getLerpPosQueue();
}
